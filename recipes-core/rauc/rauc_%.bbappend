
FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
SRC_URI += " \
    file://rel-ca.pem \
    file://dev-ca.pem \
"

do_install_append() {
    install -m 0644 ${WORKDIR}/rel-ca.pem ${D}/${sysconfdir}/rauc/
    install -m 0644 ${WORKDIR}/dev-ca.pem ${D}/${sysconfdir}/rauc/
}

