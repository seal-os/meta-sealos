SUMMARY = "SealOS manager agent running on manageable device"
HOMEPAGE = "https://github.com/opendevices/sealos-manager"
LICENSE="CLOSED"
LIC_FILES_CHKSUM=""

GO_IMPORT = "github.com/opendevices/sealos-manager"
SRC_URI = "gitsm://${GO_IMPORT};protocol=https;branch=master"

PV = "0.16.0"

SRCREV = "6b08ce1cd05ab5cc53e7644d4f0904d5fa75ad74"

GO_LINKMODE = "-X main.Version=${PV} -X main.Release=stable"

inherit go systemd

GO_INSTALL = "${GO_IMPORT}/cmd/sealos-manager"

RDEPENDS_${PN} += "bash"
RDEPENDS_${PN}-dev += "bash"

GOPRIVATE="github.com/opendevices"

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


do_configure_prepend() {
    cd ${WORKDIR}/git
    # remove mod cache as it is read-only and subsequent bitbake will fail to remove it
    echo "Clean go cache"
    ${GO} clean -modcache ./...
}

do_goclean() {
    cd ${WORKDIR}/git
    # remove mod cache as it is read-only and subsequent bitbake will fail to remove it
    echo "Clean go cache"
    ${GO} clean -modcache ./...
}
addtask goclean after do_install before do_package
do_goclean[nostamp] = "1"

do_compile() {
    cd ${WORKDIR}/git
    export TMPDIR="${GOTMPDIR}"

    for i in $(${GO} list ${WORKDIR}/git/cmd/...); do
        ${GO} install ${GOBUILDFLAGS} ${i}
    done
}

do_install() {
    install -d ${D}${libdir}/go/src/${GO_IMPORT}
    tar -C ${WORKDIR}/git -cf - --exclude-vcs --exclude '*.test' --exclude 'testdata' . | \
        tar -C ${D}${libdir}/go/src/${GO_IMPORT} --no-same-owner -xf -
    tar -C ${B} -cf - --exclude-vcs --exclude 'cache' pkg | tar -C ${D}${libdir}/go --no-same-owner -xf -


    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/git/units/* ${D}${systemd_system_unitdir}

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
    install -m 0755 ${B}/${GO_BUILD_BIN}/bin/*/* ${SEALOS_INSTALL_DIR}/
    install -m 0755 ${WORKDIR}/git/cmd/${DOWNLOAD-TOOL}/${DOWNLOAD-TOOL} ${SEALOS_INSTALL_DIR}/
    install -m 0755 ${WORKDIR}/git/cmd/${MANAGER-UPDATE-TOOL}/${MANAGER-UPDATE-TOOL}.bash ${SEALOS_INSTALL_DIR}/
    install -m 0755 ${WORKDIR}/git/cmd/${SEALOS-FS-MOUNTER}/${SEALOS-FS-MOUNTER}.bash ${SEALOS_INSTALL_DIR}/

    # Create extract Host OS directory
    install -d ${D}/data/ionoid/extract/

    # Create download Host OS directory
    install -d ${D}/data/ionoid/download/

    install -d ${D}/usr/lib/tmpfiles.d
    install -m 644 ${WORKDIR}/git/tmpfiles.d/sealos-manager.conf ${D}/usr/lib/tmpfiles.d/
    touch ${D}/data/ionoid/sealos-manager/first-boot

    if [ -e ${WORKDIR}/config.json ] ; then
        install -m 644 ${WORKDIR}/config.json ${D}/data/ionoid/boot/
    fi
}

REQUIRED_DISTRO_FEATURES= "systemd"

FILES_${PN} += "/data\
    /usr/lib/tmpfiles.d\
    ${systemd_system_unitdir}"
