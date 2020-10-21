#!/bin/bash

#
# Copyright (2020) Open Devices GmbH
# Copyright (2020) Djalal Harouni
#
# This Program is part of SealOS Manager Tools. It resizes the block device of /data partition,
# assuming that the /data partition is number 4 and last partition... change that to update.
#
# Needs lsblk, findmnt, fdisk , parted and basename
#
# https://linux.die.net/man/8/lsblk
# https://linux.die.net/man/8/findmnt
# https://linux.die.net/man/8/fdisk
# https://packages.debian.org/stretch/parted
#
# It assumes that last partition /data is the last one, if not it will fail.
#

set -xe

# Lets locate real rootfs storage
data_partition_num=4
boot_backend="$(findmnt -n -o SOURCE /boot)"
boot_line="$(lsblk -e 7 -p -n -o MAJ:MIN "$boot_backend")"
boot_backend_maj="$(echo $boot_line | cut -d':' -f1)"
sector=512

if [ -z "$boot_backend_maj" ]; then
        echo "Error: failed to locate backing store major, resize /data partition aborted"
        exit 1
fi

SEALOS_STORAGE_DEV=""
while read -r line; do
        if [[ $line == *"${boot_backend_maj}"* ]]; then
                SEALOS_STORAGE_DEV="$(echo "$line" | cut -d' ' -f1)"
                break
        fi
done < <(lsblk -e 7 -p -n -o NAME,MAJ:MIN | grep "^/dev/" -)

if [ -z "$SEALOS_STORAGE_DEV" ]; then
        echo "Error: failed to parse backing store, resize /data partition aborted"
        exit 1
fi

SEALOS_STORAGE=$(echo "$SEALOS_STORAGE_DEV" | cut -d'/' -f3)
echo "Info: found backing store at: ${SEALOS_STORAGE_DEV} - ${SEALOS_STORAGE}"

DATA_STORAGE=""
DATA_STORAGE_DEV=""
while read -r line; do
        if [[ $line == *"${boot_backend_maj}:4"* ]]; then
                DATA_STORAGE_DEV="$(echo "$line" | cut -d' ' -f1)"
        fi
done < <(lsblk -e 7 -p -n -l -o NAME,MAJ:MIN)

if [ -z "$DATA_STORAGE_DEV" ]; then
        echo "Error: failed to find /data partition num 4, resize /data partition aborted"
        exit 1
fi

DATA_STORAGE=$(echo "$DATA_STORAGE_DEV" | cut -d'/' -f3)
echo "Info: assuming /data partition at: ${DATA_STORAGE_DEV} - ${DATA_STORAGE}"

total="$(cat /sys/block/${SEALOS_STORAGE}/size)"
data_size="$(cat /sys/block/${SEALOS_STORAGE}/${DATA_STORAGE}/size)"
data_start="$(cat /sys/block/${SEALOS_STORAGE}/${DATA_STORAGE}/start)"

if [ -z "$total" ] || [ -z "$data_size" ] || [ -z "$data_start" ]; then
        echo "Error: failed to parse /data partition size, resize /data partition aborted"
        exit 1
fi

echo "Info: storage '${SEALOS_STORAGE}' size: '${total}'"
echo "Info: /data partition '${DATA_STORAGE}' current size: '${data_size}'"

data_free="$(expr "${total}" - "$(expr ${data_start} + ${data_size})" || exit 1)"

# sectors of 1 * 512 = 512 bytes

# Check if it is worth it to resize
if [ ${data_free} -le 1024 ]; then
    echo "Info: /data partition free sectors: '${data_free}' , resize /data skipped."
    exit 0
fi

# Mark partition table writable so we can resize and create backups in case
echo "w" | fdisk "${SEALOS_STORAGE}"
/usr/sbin/parted -s "${SEALOS_STORAGE}" resizepart ${data_partition_num} 100%

exit 0
