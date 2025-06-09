# Electron Version of Registro

This folder contains a prototype of the Registro application implemented with [Electron](https://www.electronjs.org/). It reimplements the original Java Swing app using web technologies.

## Setup

1. Install dependencies

```bash
npm install
```

2. Run the application

```bash
npm start
```

## Status

The prototype now supports the complete set of fields used when creando una entrada en la aplicación Java. Besides asunto, fecha y área se pueden indicar canal de entrada, áreas destinatarias y jefes, así como adjuntar ficheros de entrada, antecedentes y salida. Estos ficheros se copian a las carpetas configuradas en `CONFIG.CFG`.

Sigue ejecutándose con `--no-sandbox` y carece de interfaz avanzada, pero permite almacenar todas las propiedades principales de una entrada.

Además de crear nuevas entradas ahora es posible seleccionar una existente en la tabla principal y editarla. El formulario de edición muestra los destinatarios, jefes, archivos adjuntos y permite añadir nuevos comentarios y ficheros.
