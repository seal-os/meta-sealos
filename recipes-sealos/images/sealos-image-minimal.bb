SUMMARY = "A small image just capable of allowing a device to boot."

IMAGE_INSTALL = "bash \
                 curl \
                 coreutils \
                 dropbear \
                 file \
                 gnupg \
                 findutils \
                 iproute2 \
                 iw \
                 jq \
                 modemmanager \
                 networkmanager \
                 networkmanager-nmtui \
                 packagegroup-core-boot \
                 rauc \
                 sealos-manager \
                 sealos-update-status \
                 sudo \
                 syslog-ng \
                 tar \
                 u-boot-env \
                 u-boot-fw-utils \
                 wpa-supplicant \
                 xdelta3 \
                 ${CORE_IMAGE_EXTRA_INSTALL}"

IMAGE_LINGUAS = " "

LICENSE = "MIT"

inherit extrausers
EXTRA_USERS_PARAMS = "\
         useradd -r -m -d /data/apps/download -s /sbin/nologin sealos-download; \
         useradd -r -m -d /data/apps/store -s /sbin/nologin ionoid-app; \
         "

inherit core-image

#IMAGE_ROOTFS_SIZE = "3670016"
IMAGE_ROOTFS_EXTRA_SPACE_append = "${@bb.utils.contains("DISTRO_FEATURES", "systemd", " + 4096", "" ,d)}"
