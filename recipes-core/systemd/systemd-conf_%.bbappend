FILESEXTRAPATHS_prepend := "${THISDIR}/systemd-conf:"

SRC_URI += " \
    file://watchdog.conf \
"

do_install_append() {
    install -D -m0644 ${WORKDIR}/watchdog.conf ${D}${systemd_unitdir}/system.conf.d/01-${PN}-watchdog.conf
}
