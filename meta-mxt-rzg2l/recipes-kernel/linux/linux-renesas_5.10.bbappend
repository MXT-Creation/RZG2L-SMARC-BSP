FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

KERNEL_DEVICETREE = " \
	renesas/r9a07g044l2-mxt-smarc.dtb \
"

SRC_URI_append +=  "\
	file://dts/ \
	file://fragment-01-usb-ethernet.cfg \
	file://fragment-02-wifi.cfg \
	file://fragment-03-can.cfg \
	file://fragment-06-mscc-phy.cfg \
	file://fragment-07-lontium-lt8912b.cfg \
	file://patches/0001-drm-bridge-Introduce-LT8912B-DSI-to-HDMI-bridge.patch \
	file://patches/0002-drm-bridge-lt8912b-fix-incorrect-handling-of-of_-ret.patch \
	file://patches/0003-drm-bridge-lt8912b-fix-corrupted-image-output.patch \
"

do_compile_prepend() {
	cp -rf ${WORKDIR}/dts/* ${S}/arch/arm64/boot/dts/renesas/
}

do_install_append() {
	# This way we get a booting system, even if the camera is not the same
	install -m 0755 -d ${D}/boot
	ln -s r9a07g044l2-mxt-smarc.dtb ${D}/boot/r9a07g044l2-smarc.dtb
}

