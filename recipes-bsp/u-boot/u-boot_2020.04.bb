require recipes-bsp/u-boot/u-boot-common.inc
require recipes-bsp/u-boot/u-boot.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
SRC_URI_remove = " file://0001-dm-core-Move-ofdata_to_platdata-call-earlier.patch file://remove-redundant-yyloc-global.patch"
SRC_URI_remove = " file://0001-nanopi_neo_air_defconfig-Enable-eMMC-support.patch"
SRC_URI_append_orange-pi-zero = " file://0001-u-boot-rebase-patch-against-v2020.04.patch"
SRCREV = "36fec02b1f90b92cf51ec531564f9284eae27ab4"

DEPENDS += "bc-native dtc-native"
