SUMMARY = "Dummy input device"
SECTION = "libs"
LICENSE = "CLOSED"

inherit systemd

SRC_URI = "file://."
S = "${WORKDIR}"

TARGET_CC_ARCH += "${LDFLAGS}"

do_install() {
        oe_runmake install DESTDIR=${D} PREFIX=${exec_prefix}
        install -d ${D}${systemd_system_unitdir}
        install -m 0644 ${S}/dummy-input.service ${D}${systemd_system_unitdir}
        sed -i -e "s,@BINDIR@,${bindir},g" ${D}${systemd_system_unitdir}/dummy-input.service
}

SYSTEMD_SERVICE_${PN} = "dummy-input.service"
