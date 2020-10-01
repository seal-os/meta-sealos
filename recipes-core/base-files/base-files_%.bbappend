# find the extras files here:
FILESEXTRAPATHS_prepend := "${THISDIR}/base-files:"


SRC_URI += " \
    file://motd \
"

do_install_append() {
    install -m 0644 ${WORKDIR}/motd ${D}/etc/

    if test "x$(head -c 13 ${WORKDIR}/fstab)" = "x# stock fstab"; then
        bbwarn INSTALLING STOCK FSTAB
    fi
}
