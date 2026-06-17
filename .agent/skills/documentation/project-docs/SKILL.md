---
name: project-docs
description: Define la estructura, formato, y estilo de la documentación general del proyecto InputEngine.
trigger: Cuando se crea o modifica un archivo .md dentro de docs/.
---

# Documentación del Proyecto

## Estructura de Documentación

```
docs/
├── README.md              # Índice principal de documentación
├── client/
│   └── README.md          # Documentación de los mods client-side
└── server/
    └── README.md          # Documentación del plugin server-side
```

Adicionalmente:
- `README.md` (root) — Descripción general del proyecto, instalación, y ejemplo de API
- `Changelogs/` — Changelogs por loader (ver skill `changelog-entry`)

## Estilo de Escritura

### Audiencia
La documentación está dirigida a **dos audiencias**:
1. **Usuarios finales**: Jugadores que instalan el mod (secciones de instalación)
2. **Desarrolladores**: Programadores de plugins que usan la API (secciones de API, ejemplos de código)

### Tono
- **Conciso y directo**: Ir al grano. No sobre-explicar conceptos básicos de Minecraft modding.
- **Orientado a la acción**: Usar verbos imperativos ("Descarga", "Agrega", "Escucha").
- **Técnicamente preciso**: Nombrar clases, métodos, y paquetes correctamente con formato `code`.

### Idioma
- Documentación pública (README.md, docs/): **Inglés**
- Comentarios internos y agent skills: **Español** (idioma del equipo de desarrollo)

## Formato Markdown

### Encabezados
```markdown
# Título Principal (uno por archivo)
## Sección
### Subsección
```

### Código
- Inline: `` `ClassName` ``, `` `methodName()` ``
- Bloques: Siempre con lenguaje especificado

````markdown
```java
// Ejemplo de código Java
```
````

### Listas
- Usar `*` para listas desordenadas
- Usar `1.` para pasos secuenciales
- **Bold** para el concepto clave de cada item, seguido de descripción

### Enlaces
- Internos: `[link text](relative/path.md)`
- Externos: URL completa
- Referencias a módulos: **bold** con backticks (ej: **`client-fabric`**)

## Reglas para Documentación

1. **No duplicar información**: Si algo está documentado en el README root, no repetirlo en `docs/`. Referenciar con un enlace.

2. **Ejemplos funcionales**: Todo ejemplo de código debe ser **compilable y funcional**. No usar pseudocódigo en documentación pública.

3. **Mantener sincronizado**: Si la API cambia, actualizar la documentación en el mismo commit/PR.

4. **Estructura consistente**: Cada README de módulo debe tener:
   - Título descriptivo
   - Breve descripción del propósito
   - Secciones relevantes (funcionalidad, módulos, requisitos)

5. **Badges**: Solo en el README root. Actualmente: CodeFactor.

## Template para Documentación de Módulo

```markdown
# [Nombre del Módulo] Documentation

Brief description of what this module does and its role in the project.

## Overview

High-level explanation of functionality.

## Setup / Installation

Steps specific to this module.

## Usage

### [Feature 1]
Description + code example.

### [Feature 2]
Description + code example.

## Configuration

Any configuration options available.

## Requirements

- Required dependencies
- Compatible versions
```

## Checklist al Crear/Modificar Docs

- [ ] ¿El documento tiene un solo `#` (h1) como título?
- [ ] ¿Los ejemplos de código son funcionales y compilables?
- [ ] ¿Se usa el formato correcto de markdown (listas, código, bold)?
- [ ] ¿Se mantienen los enlaces internos correctos?
- [ ] ¿El idioma es consistente (inglés para docs públicos)?
