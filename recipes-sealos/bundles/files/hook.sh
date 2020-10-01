#!/bin/sh
case "$1" in

      slot-post-install)
              # only rootfs needs to be handled
              test "$RAUC_SLOT_CLASS" = "rootfs" || exit 0

              # preserve configuration
              cp /etc/machine-id "$RAUC_SLOT_MOUNT_POINT/etc/machine-id"
              cp /etc/dropbear/dropbear_rsa_host_key "$RAUC_SLOT_MOUNT_POINT/etc/dropbear/dropbear_rsa_host_key"

              # update sealos-manager
              MANAGER_SLOT="B"
              MANAGER_LINK_TARGET="/data/ionoid/sealos-manager/.###current"
              if [ "$MANAGER_SLOT" == "$(readlink "$MANAGER_LINK_TARGET")" ]; then
                  MANAGER_SLOT="A"
              fi
              echo "Installing sealos-manager to $MANAGER_SLOT"
              cp -afv "$RAUC_SLOT_MOUNT_POINT/data/ionoid/sealos-manager/A/"* /data/ionoid/sealos-manager/"$MANAGER_SLOT"/
              echo "Switching to to $MANAGER_SLOT"
              ln -sfnv "$MANAGER_SLOT" "$MANAGER_LINK_TARGET"

              # iterate over trust chain SPKI hashes (from leaf to root)
              for i in $RAUC_BUNDLE_SPKI_HASHES; do
                      # Test for development intermediate certificate
                      if [ "$i" == "9A:76:F9:B1:43:CF:B4:06:86:11:81:4D:69:B0:2C:79:6A:52:7C:1D:EE:A8:99:AE:55:FF:B7:6D:7B:64:AE:81" ]; then
                              echo "Activating development key chain"
                              mv ${RAUC_SLOT_MOUNT_POINT}/etc/rauc/dev-ca.pem ${RAUC_SLOT_MOUNT_POINT}/etc/rauc/ca.cert.pem
                              break
                      fi
                      # Test for release intermediate certificate
                      if [ "$i" == "87:C1:9A:6B:BF:6E:F8:6F:D4:DD:89:CB:63:11:35:03:B2:CF:F3:A9:91:D3:F3:21:7D:4C:34:B4:76:92:14:17" ]; then
                              echo "Activating release key chain"
                              mv ${RAUC_SLOT_MOUNT_POINT}/etc/rauc/rel-ca.pem ${RAUC_SLOT_MOUNT_POINT}/etc/rauc/ca.cert.pem
                              break
                      fi
              done
              ;;
        *)
              exit 1
              ;;

esac

exit 0
