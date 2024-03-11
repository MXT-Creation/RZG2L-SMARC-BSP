# Copyright (C) 2009-2015, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

DESCRIPTION ?= "Browser made by mozilla"
DEPENDS += "curl startup-notification libevent cairo libnotify \
            virtual/libgl pulseaudio icu dbus-glib \
            nodejs-native cbindgen-native \
            yasm-native nasm-native unzip-native \
            virtual/${TARGET_PREFIX}rust cargo-native ${RUSTLIB_DEP} \
           "
RDEPENDS_${PN}-dev = "dbus"

LICENSE = "MPLv2"
LIC_FILES_CHKSUM = "file://toolkit/content/license.html;endline=34;md5=dc85c0707b07153661f84c2b202c5e7f"

CVE_PRODUCT = "mozilla:firefox"

SRC_URI = "https://ftp.mozilla.org/pub/firefox/releases/${PV}/source/firefox-${PV}.source.tar.xz;name=archive \
           file://mozconfig \
           file://mozilla-firefox.png \
           file://mozilla-firefox.desktop \
           file://prefs/vendor.js \
           file://fixes/bug1545437-enable-to-specify-rust-target.patch \
           file://fixes/fix-camera-permission-dialg-doesnot-close.patch \
           file://fixes/Allow-.js-preference-files-to-set-locked-prefs-with-.patch \
           file://fixes/0001-rust-target-lexicon-0.9.0-Add-Poky-to-Vendor.patch \
           file://fixes/0002-Don-t-include-dependency-flags-in-HOST_CFLAGS-for-ru.patch \
           file://fixes/0001-Add-a-preference-to-force-enable-touch-events-withou.patch \
           file://fixes/dom-media-platforms-omx.patch \
           file://fixes/0001-Add-MOZ_GL_DISABLE_ANDROID_NATIVE_FENCE_SYNC.patch \
           file://porting/Add-xptcall-support-for-SH4-processors.patch \
           file://porting/NSS-Fix-FTBFS-on-Hurd-because-of-MAXPATHLEN.patch \
           file://porting/Use-NEON_FLAGS-instead-of-VPX_ASFLAGS-for-libaom-neo.patch \
           file://porting/Work-around-GCC-ICE-on-mips-i386-and-s390x.patch \
           file://porting/Work-around-another-GCC-ICE-on-arm.patch \
           file://porting/Bug-1822827-Remove-explicit-NEON-flags-from-skia-bui.patch \
           file://porting/Use-compiler-macros-to-detect-big-endian.patch \
           file://prefs/Set-DPI-to-system-settings.patch \
           file://prefs/Don-t-auto-disable-extensions-in-system-directories.patch \
           file://debian-hacks/Avoid-wrong-sessionstore-data-to-keep-windows-out-of.patch \
           file://debian-hacks/Add-another-preferences-directory-for-applications-p.patch \
           file://debian-hacks/Add-a-2-minutes-timeout-on-xpcshell-tests.patch \
           file://debian-hacks/Don-t-build-image-gtests.patch \
           file://debian-hacks/Use-the-Mozilla-Location-Service-key-when-the-Google.patch \
           file://debian-hacks/Avoid-using-vmrs-vmsr-on-armel.patch \
           file://debian-hacks/Use-build-id-as-langpack-version-for-reproducibility.patch \
           file://debian-hacks/Allow-to-build-with-older-versions-of-nodejs-10.patch \
           file://debian-hacks/Add-missing-webrtc-directory-for-ppc64el-bz-1775202.patch \
           file://debian-hacks/Allow-to-override-rust-LTO-flag.patch \
           file://debian-hacks/Remove-workaround-for-old-libstdc-problem-which-now-.patch \
           file://debian-hacks/Work-around-https-sourceware.org-bugzilla-show_bug.c.patch \
           file://wayland/egl/bug1571603-Disable-eglQueryString-nullptr-EGL_EXTENSIONS.patch \
           file://wayland/egl/0001-GLLibraryLoader-Use-given-symbol-lookup-function-fir.patch \
           file://wayland/egl/0001-Mark-GLFeature-framebuffer_multisample-as-unsupporte.patch \
           file://wayland/firefox-wayland.sh \
           "

SRC_URI += "file://fixes/0001-Add-MOZ_GLXTEST_RESULT-environment-variable.patch \
            file://fixes/0001-Add-MOZ_WAYLAND_GL_LIBRARY-environment-variable.patch \
            file://fixes/0001-Add-YUV420SemiPlanar-support-for-OMX.patch \
            file://fixes/0002-omx-Add-temporary-workaround-for-RZ-G2L.patch \
            "

SRC_URI[archive.md5sum] = "300c5e3295aacb4133b8c0499acc2c97"
SRC_URI[archive.sha256sum] = "09194fb765953bc6979a35aa8834118c453b9d6060bf1ec4e134551bad740113"

S = "${WORKDIR}/firefox-${MOZ_APP_BASE_VERSION}"

MOZ_APP_BASE_VERSION = "${@'${PV}'.replace('esr', '')}"

inherit mozilla rust-common

TOOLCHAIN_pn-firefox = "clang"
AS = "${CC}"

DISABLE_STATIC=""

ARM_INSTRUCTION_SET_armv5 = "arm"

PACKAGECONFIG ??= "${@bb.utils.contains("DISTRO_FEATURES", "alsa", "alsa", "", d)} \
                   ${@bb.utils.contains("DISTRO_FEATURES", "wayland", "wayland", "", d)} \
                   ${@bb.utils.contains_any("TARGET_ARCH", "x86_64 arm aarch64", "webrtc", "", d)} \
"
PACKAGECONFIG[alsa] = "--enable-alsa,--disable-alsa,alsa-lib"
PACKAGECONFIG[wayland] = "--enable-default-toolkit=cairo-gtk3-wayland,--enable-default-toolkit=cairo-gtk3,virtual/egl,"
PACKAGECONFIG[gpu] = ",,,"
PACKAGECONFIG[openmax] = "--enable-openmax,,,"
PACKAGECONFIG[webgl] = ",,,"
PACKAGECONFIG[webrtc] = "--enable-webrtc,--disable-webrtc,,"
PACKAGECONFIG[forbit-multiple-compositors] = ",,,"

# Add a config file to enable GPU acceleration by default.
SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'gpu', \
           'file://prefs/gpu.js', '', d)}"

# Additional upstream patches to support OpenMAX IL
SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'openmax', \
           'file://fixes/Bug-1590977-openmax-Import-latest-OpenMAX-IL-1.1.2-headers.patch \
            file://prefs/openmax.js \
           ', '', d)}"

SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'webgl', \
           'file://prefs/webgl.js', '', d)}"

SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'forbit-multiple-compositors', \
           'file://prefs/single-compositor.js \
            file://fixes/0001-Enable-to-suppress-multiple-compositors.patch \
	   ', '', d)}"

do_install_append() {
    install -d ${D}${datadir}/applications
    install -d ${D}${datadir}/pixmaps

    install -m 0644 ${WORKDIR}/mozilla-firefox.desktop ${D}${datadir}/applications/
    install -m 0644 ${WORKDIR}/mozilla-firefox.png ${D}${datadir}/pixmaps/
    install -m 0644 ${WORKDIR}/prefs/vendor.js ${D}${libdir}/${PN}/defaults/pref/
    if [ -n "${@bb.utils.contains_any('PACKAGECONFIG', 'gpu', '1', '', d)}" ]; then
        install -m 0644 ${WORKDIR}/prefs/gpu.js ${D}${libdir}/${PN}/defaults/pref/
    fi
    if [ -n "${@bb.utils.contains('PACKAGECONFIG', 'openmax', '1', '', d)}" ]; then
        install -m 0644 ${WORKDIR}/prefs/openmax.js ${D}${libdir}/${PN}/defaults/pref/
    fi
    if [ -n "${@bb.utils.contains('PACKAGECONFIG', 'webgl', '1', '', d)}" ]; then
        install -m 0644 ${WORKDIR}/prefs/webgl.js ${D}${libdir}/${PN}/defaults/pref/
    fi
    if [ -n "${@bb.utils.contains('PACKAGECONFIG', 'forbit-multiple-compositors', '1', '', d)}" ]; then
        install -m 0644 ${WORKDIR}/prefs/single-compositor.js ${D}${libdir}/${PN}/defaults/pref/
    fi
    if [ -n "${@bb.utils.contains('PACKAGECONFIG', 'wayland', '1', '', d)}" ]; then
        install -d ${D}${sysconfdir}/profile.d
        install -m 0755 ${WORKDIR}/wayland/firefox-wayland.sh ${D}${sysconfdir}/profile.d/
    fi

    # Fix ownership of files
    chown root:root -R ${D}${datadir}
    chown root:root -R ${D}${libdir}
}

FILES_${PN} = "${bindir}/${PN} \
               ${datadir}/applications/ \
               ${datadir}/pixmaps/ \
               ${libdir}/${PN}/* \
               ${libdir}/${PN}/.autoreg \
               ${bindir}/defaults \
               ${sysconfdir}/profile.d/*"
FILES_${PN}-dev += "${datadir}/idl ${bindir}/${PN}-config ${libdir}/${PN}-devel-*"
FILES_${PN}-staticdev += "${libdir}/${PN}-devel-*/sdk/lib/*.a"
FILES_${PN}-dbg += "${libdir}/${PN}/.debug \
                    ${libdir}/${PN}/*/.debug \
                    ${libdir}/${PN}/*/*/.debug \
                    ${libdir}/${PN}/*/*/*/.debug \
                    ${libdir}/${PN}-devel-*/*/.debug \
                    ${libdir}/${PN}-devel-*/*/*/.debug \
                    ${libdir}/${PN}-devel-*/*/*/*/.debug \
                    ${libdir}/${PN}/fix_linux_stack.py \
                    ${bindir}/.debug"

# We don't build XUL as system shared lib, so we can mark all libs as private
PRIVATE_LIBS = " \
    libmozjs.so \
    libxpcom.so \
    libnspr4.so \
    libxul.so \
    libmozalloc.so \
    libplc4.so \
    libplds4.so \
    liblgpllibs.so \
    libmozgtk.so \
    libmozwayland.so \
    libmozsqlite3.so \
    libclearkey.so \
    libmozavcodec.so \
    libmozavutil.so \
"

# mark libraries also provided by nss as private too
PRIVATE_LIBS += " \
    libfreebl3.so \
    libfreeblpriv3.so \
    libnss3.so \
    libnssckbi.so \
    libsmime3.so \
    libnssutil3.so \
    libnssdbm3.so \
    libssl3.so \
    libsoftokn3.so \
"

CLEANBROKEN = "1"