# Use Red Hat UBI Python 2.7 to avoid Docker Hub rate limits
FROM registry.access.redhat.com/ubi7/python-27:latest

USER root

WORKDIR /app

# Copy requirements and install
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy application
COPY app.py .

# Expose port
EXPOSE 8080

# Set environment
ENV PORT=8080

# Run application
CMD ["python", "app.py"]

# Made with Bob
