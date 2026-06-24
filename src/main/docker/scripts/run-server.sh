#!/bin/bash
echo "$(date -u +"%Y-%m-%dT%H:%M:%SZ")"
[[ -z "${KUBERNETES_TRUST_PATTERN}" ]] && KUBERNETES_TRUST_PATTERN="/run/secrets/kubernetes.io/serviceaccount/*.crt"
[[ -z "${TRUST_PATTERN}" ]] && TRUST_PATTERN="/etc/k5/security/truststore/*"
[[ -z "${TRUSTSTORE_FILE}" ]] && TRUSTSTORE_FILE="/app/cert/trust.jks"
[[ -z "${SERVER_SSL_PEM_KEY}" ]] && TRUSTSTORE_FILE="/etc/k5/security/servicecert/tls.key"

function sleepExit() {
  echo "Give kubernetes a chance to process the logs..."
  sleep 2
  exit $1
}

function checkExists() {
  local VALUE="$1"
  local NAME="$2"
  local PRINTABLE="$3"
  if [[ "${PRINTABLE}" == "true" ]]; then echo "[${NAME}=${VALUE}]"; else echo "[${NAME}=***]"; fi
  if [[ -z "${VALUE}" ]]; then
    echo "${NAME} is undefined!"
    sleepExit 42
  fi
}

function importTrustCerts() {
  local SRC_PATTERN="$1"
  local KS_FILE="$2"
  local KS_PWD="$3"

  checkExists "${SRC_PATTERN}" "SRC_PATTERN" "true"
  checkExists "${KS_FILE}" "KS_FILE" "true"
  checkExists "${KS_PWD}" "KS_PWD" "false"

  echo "Searching for ${SRC_PATTERN} for certificates"
  for file in $(ls -1 ${SRC_PATTERN}); do
    echo "Processing certificate file [file=${file}]"
    local WRK_DIR=$(mktemp -d)
    local FILE_BASENAME=$(basename "${file}")

    checkExists "${file}" "file" "true"
    checkExists "${WRK_DIR}" "WRK_DIR" "true"
    checkExists "${FILE_BASENAME}" "FILE_BASENAME" "true"

    echo "Splitting ${file} to ${WRK_DIR}..."
    cat "${file}" |
      awk -v WRK_DIR="${WRK_DIR}" -v FILE_BASENAME="${FILE_BASENAME}" 'BEGIN {c=0;} /BEGIN CERT/{c++} { print > WRK_DIR "/" FILE_BASENAME "." c ".crt"}'

    local count=0
    for splittedfile in $(ls -1 ${WRK_DIR}/*.crt); do
      local TS_ALIAS="${file}.${count}"
      echo "Importing ${file} to ${KS_FILE} as alias ${TS_ALIAS} from ${splittedfile}..."
      keytool -import -noprompt -trustcacerts -alias "${TS_ALIAS}" -file "${splittedfile}" -keystore "${KS_FILE}" -storepass "${KS_PWD}"
      RC=$?
      if [[ "$RC" != "0" ]]; then
        echo "WARNING: Failed add certificate: ${splittedfile} [file=${file}, entry=${count}] - Ignore it"
      fi
      ((count = count + 1))
    done
  done
}

function importKeyCert() {
  local SRC_PRIV_PEM_FILE="$1"
  local SRC_PUB_PEM_FILE="$2"
  local DST_KEYSTORE_FILE="$3"
  local DST_ALIAS="$4"
  local STORE_PWD="$5"

  checkExists "${SRC_PRIV_PEM_FILE}" "SRC_PRIV_PEM_FILE" "true"
  checkExists "${SRC_PUB_PEM_FILE}" "SRC_PUB_PEM_FILE" "true"
  checkExists "${DST_KEYSTORE_FILE}" "DST_KEYSTORE_FILE" "true"
  checkExists "${DST_ALIAS}" "DST_ALIAS" "true"
  checkExists "${STORE_PWD}" "STORE_PWD" "false"

  if [[ ! -s "${SRC_PRIV_PEM_FILE}" ]]; then
    echo "WARNING: ${SRC_PRIV_PEM_FILE} does not exists or is empty"
    #sleepExit 42
  fi

  if [[ ! -s "${SRC_PUB_PEM_FILE}" ]]; then
    echo "WARNING: ${SRC_PUB_PEM_FILE} does not exists or is empty"
    #sleepExit 42
  fi

  echo "Create pkcs12 ${DST_KEYSTORE_FILE} with alias ${DST_ALIAS}"
  cat "${SRC_PRIV_PEM_FILE}" "${SRC_PUB_PEM_FILE}" |
    openssl pkcs12 -export -out "${DST_KEYSTORE_FILE}" -passout "pass:${STORE_PWD}" -name "${DST_ALIAS}"
  RC=$?
  if [[ "$RC" != "0" ]]; then
    echo "WARNING: Failed to create pkcs12 [RC=${RC}, COMBINED_PEM_FILE=${COMBINED_PEM_FILE}, PKCS_FILE=${PKCS_FILE}]"
    #sleepExit 42
  fi

}

function listStore() {
  local KS_FILE="$1"
  local KS_PWD="$2"

  checkExists "${KS_FILE}" "KS_FILE" "true"
  checkExists "${KS_PWD}" "KS_PWD" "false"

  echo "List entries of ${KS_FILE}"
  keytool -list -keystore "${KS_FILE}" -storepass "${KS_PWD}"
}

function getDefaultJavaTruststore() {
  # Find the default Java truststore (cacerts)
  # Returns the path to the truststore file, or empty string if not found
  local JAVA_HOME="${JAVA_HOME:-$(java -XshowSettings:properties -version 2>&1 | grep 'java.home' | awk '{print $3}')}"
  if [[ -z "${JAVA_HOME}" ]]; then
    JAVA_HOME=$(readlink -f $(which java) 2>/dev/null | sed 's|/bin/java||')
  fi
  local DEFAULT_TRUSTSTORE=""

  # Try common locations for cacerts
  local POSSIBLE_PATHS=(
    "${JAVA_HOME}/lib/security/cacerts"
    "${JAVA_HOME}/jre/lib/security/cacerts"
    "/usr/lib/jvm/default-java/lib/security/cacerts"
    "/etc/ssl/certs/java/cacerts"
  )

  # Also check for java-* directories
  for java_dir in /usr/lib/jvm/java-*; do
    if [[ -d "${java_dir}" ]]; then
      POSSIBLE_PATHS+=("${java_dir}/lib/security/cacerts")
      POSSIBLE_PATHS+=("${java_dir}/jre/lib/security/cacerts")
    fi
  done

  for path in "${POSSIBLE_PATHS[@]}"; do
    if [[ -f "${path}" ]]; then
      DEFAULT_TRUSTSTORE="${path}"
      break
    fi
  done

  echo "${DEFAULT_TRUSTSTORE}"
}

function checkForExistence() {
  local FILE="$1"
  local CERTIFICATES_ONLY="$2"
  local PASSWORD="$3"
  if [[ ! -f "${FILE}" ]]; then
    echo "no keystore file found at ${FILE}, creating a new one"
    mkdir -p $(dirname "${FILE}")

    if [[ "${CERTIFICATES_ONLY}" == "true" ]]; then
      echo "Trying to copy default Java truststore from ${DEFAULT_TRUSTSTORE} to ${FILE}"
      local DEFAULT_TRUSTSTORE=$(getDefaultJavaTruststore)
      echo "found DEFAULT_TRUSTSTORE: ${DEFAULT_TRUSTSTORE}"
      if [[ -n "${DEFAULT_TRUSTSTORE}" && -f "${DEFAULT_TRUSTSTORE}" ]]; then
        echo "Copying default Java truststore from ${DEFAULT_TRUSTSTORE} to ${FILE}"
        cp "${DEFAULT_TRUSTSTORE}" "${FILE}"
      fi
    else
      # Create empty keystore with a self-signed certificate
      echo "Creating empty keystore at ${FILE} with a self-signed certificate"
      keytool -genkeypair -keyalg RSA -noprompt -alias "servicekey" -dname "CN=localhost" -keystore "${FILE}" -storepass "${PASSWORD}" -keypass "${PASSWORD}" -validity 90
    fi
  fi
}

NEW_TRUSTSTORE_PW=$(openssl rand -base64 16)

checkForExistence "${TRUSTSTORE_FILE}" "true"

keytool -storepasswd -new "${NEW_TRUSTSTORE_PW}" \
  -storepass "changeit" \
  -keystore "${TRUSTSTORE_FILE}"

echo "$(date -u +"%Y-%m-%dT%H:%M:%SZ")"
importTrustCerts "${KUBERNETES_TRUST_PATTERN}" "${TRUSTSTORE_FILE}" "${NEW_TRUSTSTORE_PW}"
echo "$(date -u +"%Y-%m-%dT%H:%M:%SZ")"
importTrustCerts "${TRUST_PATTERN}" "${TRUSTSTORE_FILE}" "${NEW_TRUSTSTORE_PW}"
echo "$(date -u +"%Y-%m-%dT%H:%M:%SZ")"
listStore "${TRUSTSTORE_FILE}" "${NEW_TRUSTSTORE_PW}"

if [[ -z "${SERVER_SSL_PEM_KEY}" && -z "${SERVER_SSL_PEM_CRT}" ]]; then
  echo "INFO: SERVER_SSL_PEM_KEY and/or SERVER_SSL_PEM_CRT are empty: skip creation of keystore"
else
  echo "Creating keystore"
  [[ -z "${SERVER_SSL_KEYSTOREPASSWORD}" ]] && export SERVER_SSL_KEYSTOREPASSWORD=$(openssl rand -base64 16)
  [[ -z "${SERVER_SSL_KEYSTORETYPE}" ]] && export SERVER_SSL_KEYSTORETYPE="PKCS12"
  [[ -z "${SERVER_SSL_KEYSTORE}" ]] && export SERVER_SSL_KEYSTORE="/app/cert/key.p12"
  [[ -z "${SERVER_SSL_KEYALIAS}" ]] && export SERVER_SSL_KEYALIAS="servicekey"

  checkForExistence "${SERVER_SSL_KEYSTORE}" "false" "${SERVER_SSL_KEYSTOREPASSWORD}"

  echo "$(date -u +"%Y-%m-%dT%H:%M:%SZ")"
  importKeyCert "${SERVER_SSL_PEM_KEY}" "${SERVER_SSL_PEM_CRT}" "${SERVER_SSL_KEYSTORE}" "${SERVER_SSL_KEYALIAS}" "${SERVER_SSL_KEYSTOREPASSWORD}"
  listStore "${SERVER_SSL_KEYSTORE}" "${SERVER_SSL_KEYSTOREPASSWORD}"
fi

export JAVA_OPTIONS="${JAVA_OPTIONS} \
	-Dfile.encoding=UTF-8 \
	-Djava.security.egd=file:/dev/./urandom \
	-Djavax.net.ssl.trustStore=${TRUSTSTORE_FILE} \
	-Djavax.net.ssl.trustAnchors=${TRUSTSTORE_FILE} \
	-Djavax.net.ssl.trustStorePassword=${NEW_TRUSTSTORE_PW} \
	-Dkubernetes.master=https://${KUBERNETES_SERVICE_HOST}:${KUBERNETES_SERVICE_PORT_HTTPS} \
	-Dsun.jnu.encoding=UTF-8 \
	-Duser.country=US \
	-Duser.language=en \
	-Duser.timezone=UTC \
	-Duser.variant= \
"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"

# kubernetes sends a SIGTERM signal to this shell script to signal that the pod is scheduled for termination.
# We need to forward this signal to the run-java.sh script because the shell doesn't do so automatically.
# see https://unix.stackexchange.com/questions/146756/forward-sigterm-to-child-in-bash
_term() {
  echo "Caught SIGTERM signal!"
  kill -TERM "$runJavaPid" 2>/dev/null # send a sigterm signal to run-java.sh
  wait "$runJavaPid"                   # wait until run-java.sh terminated; otherwise this script would end before the application had sufficient time to terminate
}
trap _term SIGTERM

#echo "Java settings"
#java -XshowSettings

echo "Java version"
java -version

echo "$(date -u +"%Y-%m-%dT%H:%M:%SZ")"
echo "Starting app via run-java.sh in folder ${SCRIPT_DIR} "
${SCRIPT_DIR}/run-java.sh "$@" &

# Wait here until run-java.sh exits or until we receive a SIGTERM signal.
# In the latter case wait blocks until the _term trap finished (which in turn waits until the application terminated)
runJavaPid=$!
wait "$runJavaPid"

exitStatus=$?
echo "$(date -u +"%Y-%m-%dT%H:%M:%SZ")"
echo "[exitStatus=${exitStatus}]"

sleepExit ${exitStatus}
