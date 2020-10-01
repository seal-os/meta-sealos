FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
PACKAGECONFIG_append = " modemmanager ppp"

SRC_URI += "file://0001-Depend-on-ModemManager.patch"

