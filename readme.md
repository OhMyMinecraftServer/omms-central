# OMMS Central Server
[![State-of-the-art Shitcode](https://img.shields.io/static/v1?label=State-of-the-art&message=Shitcode&color=7B5804)](https://github.com/trekhleb/state-of-the-art-shitcode)
### Thanks to ishland, MapleDust and shenjack

This is Central Server of Oh My Minecraft Server project, written in Kotlin and Java.  
Central Server provides the implementation of server management, player whitelist management, etc.  
Oh My Minecraft Server aims to provide a solution for manage Minecraft servers.

## Features
+ Remote Server Console
+ Player Whitelist
+ In-game Announcement
+ Chat Bridge
+ Plugins
+ Server Status

## Installation

Download the latest build from Github Action, and launch it:
```shell
$ java -jar <path-to-your-omms-central-server-jar.jar> --nogui
```

It will generate default configurations, fill those configuration, and you are good to go!

```shell
$ java -jar <path-to-your-omms-central-server-jar.jar>
```

## Deployment

Oh My Minecraft Server Central Server requires `java >= 17`

### Command Line Arguments
+ `--nogui` Do not display simple gui
+ `--noplugin` Disable Plugins
+ `--controllerConsole <controllerId>` Launch a server console

## Contributing

PRs are welcomed.Each contribution to this repository should take the form of a pull request to review codes.