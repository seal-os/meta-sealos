inherit goarch systemd

SUMMARY = "SealOS manager agent running on manageable device"
HOMEPAGE = "https://github.com/opendevices/sealos-manager"
LICENSE="CLOSED"
LIC_FILES_CHKSUM=""

ARTIFACT_NAME = "sealos-manager-${PV}-linux-${TARGET_ARCH}${TARGET_GOARM}"

SRC_URI = "https://raw.githubusercontent.com/opendevices/packages/master/sealos-manager/download/stable/${PV}/${ARTIFACT_NAME}.zip"
SRC_URI[sha256sum] = "e08eabe24d2ae64e09113d1761d7c6213297baf3bce2bf3b06d958c2f74cd357"

RDEPENDS_${PN} += "bash"
RDEPENDS_${PN}-dev += "bash"

BBCLASSEXTEND = "native nativesdk"

DOWNLOAD-TOOL = "sealos-download"
MANAGER-UPDATE-TOOL = "sealos-manager-update"
SEALOS-FS-MOUNTER = "sealos-fs-mounter"

SYSTEMD_SERVICE_${PN} = "sealos-manager.service\
    sealos-manager-journal.path\
    sealos-manager-actions.path\
    sealos-boot-setup.service\
    sealos-fs-mounter.service\
    sealos-clean-files.timer"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

INSANE_SKIP_${PN} += "already-stripped"

do_install() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/${ARTIFACT_NAME}/units/* ${D}${systemd_system_unitdir}

    install -d ${D}/data/system/etc/
    install -d ${D}/data/system/var/
    install -d ${D}/data/apps/download/tmp/
    install -d ${D}/data/ionoid/boot/
    install -d ${D}/data/ionoid/extract/
    install -d ${D}/data/ionoid/download/
    install -d ${D}/data/ionoid/sealos-manager/
    install -d ${D}/data/ionoid/sealos-manager/A/usr/lib/
    install -d ${D}/data/ionoid/sealos-manager/B/usr/lib/

    cd ${D}/data/ionoid/sealos-manager
    ln -sr ./A ./.###current
    cd -

    SEALOS_INSTALL_DIR=${D}/data/ionoid/sealos-manager/.###current/usr/lib/
    install -d ${SEALOS_INSTALL_DIR}
    install -m 0755 ${WORKDIR}/${ARTIFACT_NAME}/build/${TARGET_ARCH}${TARGET_GOARM}/* ${SEALOS_INSTALL_DIR}/

    # Create extract Host OS directory
    install -d ${D}/data/ionoid/extract/

    # Create download Host OS directory
    install -d ${D}/data/ionoid/download/

    install -d ${D}/usr/lib/tmpfiles.d
    install -m 644 ${WORKDIR}/${ARTIFACT_NAME}/tmpfiles.d/sealos-manager.conf ${D}/usr/lib/tmpfiles.d/
    touch ${D}/data/ionoid/sealos-manager/first-boot

    if [ -e ${WORKDIR}/config.json ] ; then
        install -m 644 ${WORKDIR}/config.json ${D}/data/ionoid/boot/
    fi
}

REQUIRED_DISTRO_FEATURES= "systemd"

FILES_${PN} += "/data\
    /usr/lib/tmpfiles.d\
    ${systemd_system_unitdir}"
