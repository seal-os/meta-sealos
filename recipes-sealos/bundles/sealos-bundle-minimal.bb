inherit bundle

LICENSE = "CLOSED"

SRC_URI += "file://hook.sh"

RAUC_BUNDLE_COMPATIBLE = "sealos-mypi-integrator-board"
#RAUC_BUNDLE_VERSION ?= "v2015-06-07-1"
RAUC_BUNDLE_HOOKS[file] ?= "hook.sh"


RAUC_BUNDLE_SLOTS = "rootfs"
RAUC_SLOT_rootfs = "sealos-image-minimal"
RAUC_SLOT_rootfs[fstype] = "ext4"
RAUC_SLOT_rootfs[hooks] ?= "post-install"
