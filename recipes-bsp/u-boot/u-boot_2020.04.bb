require recipes-bsp/u-boot/u-boot-common.inc
require recipes-bsp/u-boot/u-boot.inc

SRC_URI_remove_raspberrypi-cm3 = " file://0001-dm-core-Move-ofdata_to_platdata-call-earlier.patch file://remove-redundant-yyloc-global.patch"
SRCREV = "36fec02b1f90b92cf51ec531564f9284eae27ab4"

DEPENDS += "bc-native dtc-native"
