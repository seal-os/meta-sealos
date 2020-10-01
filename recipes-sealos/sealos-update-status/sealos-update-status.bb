SUMMARY = "Post system update checks"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://../LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI = "file://sealos-update-check-no-failures.bash \
           file://LICENSE \
           file://sealos-update-check-no-failures.service"

inherit systemd

RDEPENDS_${PN} = "bash"

SYSTEMD_SERVICE_${PN} = "sealos-update-check-no-failures.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

do_install() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/sealos-update-check-no-failures.service ${D}${systemd_system_unitdir}

    install -d ${D}/usr/bin
    install -m 0744 ${WORKDIR}/sealos-update-check-no-failures.bash ${D}/usr/bin/
}

REQUIRED_DISTRO_FEATURES= "systemd"

FILES_${PN} += "/usr/bin/ \
    ${systemd_system_unitdir}"
