# SealOS Yocto layers

SealOS is a Secure Linux Operating System for IoT and Edge devices.

## Introduction

This repository contains various [Yocto](https://www.yoctoproject.org/) meta layer files to build SealOS for different devices.


## Build SealOS

To build SealOS according to Yocto supported versions:

- Yocto Dunfell: use the [dunfell tree branch](https://github.com/seal-os/meta-sealos/tree/dunfell)


### Build managed SealOS

[Ionoid.io](https://ionoid.io) SealOS' versions come with `sealos-manager` installed to perform remote device managment,
and OTA system updates.

You can always build your own SealOS verion that does not include `sealos-manager` daemon.
