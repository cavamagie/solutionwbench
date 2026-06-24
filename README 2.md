# Template Java Domain-Driven Design (javatemplateddd)

Template for implementing a Java Domain-Driven Design service with a Coding Assistant.

## Using This Template with a Coding Assistant

This template is designed to work with a coding assistant that supports custom skills. The project includes specialized generation skills (k5-generate-*) that automatically generate Java Domain-Driven Design components from design files.

### Prerequisites

- Design files located in `/src-design` directory (representing your domain model, entities, commands, events, etc.)
- Project configuration defined in `k5-project.yml`
- A coding assistant with access to the k5-generate-* skills

### Available Generation Skills

The template provides the following generation skills:

1. **k5-generate** - Orchestrates the generation process and calls all core generation skills
2. **k5-generate-entities** - Generates domain entities from design files
3. **k5-generate-persistence-layer** - Creates MongoDB persistence layer (repositories, converters)
4. **k5-generate-commands** - Generates command classes for domain operations
5. **k5-generate-domain-services** - Creates domain service implementations
6. **k5-generate-rest-api** - Generates REST API controllers with Swagger documentation
7. **k5-generate-eventing** - Creates Apache Kafka event publishers and consumers

### Optional Generation Skills

These skills can be invoked separately as needed:

- **k5-generate-unit-tests** - Generates comprehensive unit tests for all components
- **k5-generate-readme** - Creates detailed project documentation and README

### Supporting Guidelines

Two foundational skills ensure consistency and quality:

- **k5-coding-guidelines** - Defines coding standards for all generated code (Java/Spring Boot, JavaDoc documentation, SLF4J logging, naming conventions). Automatically applied by all generation skills.

- **k5-generation-guidelines** - Orchestrates the generation workflow: updates configuration files, replaces template placeholders with project name, configures Docker Compose and Helm charts, validates build with Maven, and reports generation status.

### Generation Process

To generate code from your design files, simply prompt your coding assistant:

```
k5-generate
```

The coding assistant will:
1. Read your design files from `/src-design`
2. Execute core generation skills in the proper order: 
   - k5-generate-entities
   - k5-generate-persistence-layer
   - k5-generate-commands
   - k5-generate-domain-services
   - k5-generate-rest-api
   - k5-generate-eventing
3. Apply consistent coding guidelines across all generated code
4. Update configuration files (application.yaml, values.yaml) with project-specific values
5. Replace template placeholders with your actual project name
6. Compile the project with Maven to validate generated code
7. Report generation summary and build status

**Note**: Unit tests and README generation are optional. Invoke `k5-generate-unit-tests` and `k5-generate-readme` separately if needed.

### What Gets Generated

- **Domain Layer**: Entities, value objects, aggregates, domain services
- **Application Layer**: Commands, command handlers, application services
- **Infrastructure Layer**: MongoDB repositories, data converters, Kafka producers/consumers
- **API Layer**: REST controllers with OpenAPI/Swagger documentation
- **Tests**: Comprehensive unit tests for all components
- **Configuration**: Updated Spring Boot configuration files
- **Documentation**: Complete README with API documentation and setup instructions

### Customization

After generation, the coding assistant will:
- Update all configuration files with your project name from `k5-project.yml`
- Configure Docker Compose files for local development
- Set up Helm charts for Kubernetes deployment
- Ensure consistent naming conventions throughout the codebase

### Manual Invocation of Individual Skills

You can invoke individual generation skills for specific components:

**Core Skills** (included in k5-generate):
```
k5-generate-entities
k5-generate-persistence-layer
k5-generate-commands
k5-generate-domain-services
k5-generate-rest-api
k5-generate-eventing
```

**Optional Skills** (must be invoked separately):
```
k5-generate-unit-tests
k5-generate-readme
```

This allows for incremental development and regeneration of specific components as your design evolves.
