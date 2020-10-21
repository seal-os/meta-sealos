SUMMARY = "Resize data partition"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://../LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI = "file://sealos-resize-data-block.service \
           file://sealos-resize-data.bash \
           file://LICENSE \
           file://sealos-resize-data-fs.service"

inherit systemd
inherit features_check

SYSTEMD_SERVICE_${PN} = "sealos-resize-data-block.service\
    sealos-resize-data-fs.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

do_install() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/sealos-resize-data-block.service ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/sealos-resize-data-fs.service ${D}${systemd_system_unitdir}

    install -d ${D}/usr/bin
    install -m 0744 ${WORKDIR}/sealos-resize-data.bash ${D}/usr/bin/
}

RDEPENDS_${PN} = "bash util-linux-findmnt util-linux-lsblk parted"

REQUIRED_DISTRO_FEATURES= "systemd"

FILES_${PN} += "/usr/bin/ \
    ${systemd_system_unitdir}"

