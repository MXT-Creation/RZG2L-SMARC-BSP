SUMMARY = "Browser packages for RZ MPU"
LICENSE = "MIT"

inherit packagegroup

PACKAGES = " \
        packagegroup-rz-browser \
        "

RDEPENDS_packagegroup-rz-browser = " \
	firefox \
	ttf-sazanami-gothic \
	ttf-sazanami-mincho \
	pulseaudio-server \
	adwaita-icon-theme \
	adwaita-icon-theme-cursors \
	"
