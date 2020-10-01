#!/bin/bash

#
# Copyright (2020) Open Devices GmbH
# Copyright (2020) Djalal Harouni
#


declare RUNTIME_ARGS="$@"
declare UPDATE_STATUS="/data/ionoid/boot/update-os.status"

export PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin


check_exit_status() {
        local exit_code=$?
        if [ $exit_code -ne 0 ]; then
                #
                # Always remove update status on failures/rollback
                # so we do not trigger it next reboot (rollback)
                #
                rm -f "${UPDATE_STATUS}"

                echo "Error: check update status: [failed]"
                reboot
        else
                echo "Info: check update status: [succeeded]"
        fi

        exit $exit_code
}

check_failed_units() {
        out=$(systemctl --failed --all 2>/dev/null)
        if [[ $out != *"0 loaded units"* ]]; then
                echo "Error: check system units failed; update will be aborted"
                exit 1
        fi
}

check_system_status() {

        # check status and exit 1 on failures
        mountpoint /boot
        mountpoint /data
        fw_printenv

        #
        # If update was good lets mark status finished so we can notify backend
        # About successful updates in case.
        # 
        mkdir -p /data/ionoid/boot/
        echo -n "STATUS=\"finished\"" >> "${UPDATE_STATUS}"
        return
}

main() {

        source ${UPDATE_STATUS}
        if [ "${STATUS}" = "finished" ]; then
                echo "Info: update already performed and finished with success"
                rm -f "${UPDATE_STATUS}"
                exit 0
        fi

        trap check_exit_status EXIT

        check_failed_units

        check_system_status

        exit 0
}

main $RUNTIME_ARGS
