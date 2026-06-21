# Server Documentation

This section provides information about the server-side plugin for **Spigot**.

## Module: plugin-spigot

The `plugin-spigot` module is built against the Spigot API. 

## Functionality
The server plugin listens for custom payload packets sent from the client mods (Fabric and NeoForge). Once an input is received, the server can process it or expose it to other server-side plugins via an API.

## API Usage
If you are a developer looking to integrate InputEngine into your own plugins, please read the [API Documentation](api/README.md).
