FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI_append +=  "\
	file://fragment-07-lontium-lt8912b.cfg \
	file://patches/0001-drm-bridge-Introduce-LT8912B-DSI-to-HDMI-bridge.patch \
"

