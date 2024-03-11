ROOTFS_POSTPROCESS_COMMAND += 'set_firefox_environment;'
set_firefox_environment() {
    if [ -e ${IMAGE_ROOTFS}/${libdir}/firefox ] || [ -e ${IMAGE_ROOTFS}/${libdir}/webviewer ]; then
        cat <<EOF >> ${IMAGE_ROOTFS}/etc/environment
MOZ_ENABLE_WAYLAND=1
MOZ_WAYLAND_GL_LIBRARY=libGLESv2.so
MOZ_WEBRENDER=1
EOF

        case "${MACHINE}" in
            ek874)
                echo 'MOZ_GLXTEST_RESULT=VENDOR\\nProprietary\\nRENDERER\\nHardware\\nVERSION\\nN/A\\nTFP\\nTRUE\\nSCREEN_INFO\\n1280x720:1;\\nTEST_TYPE\\nEGL' >> ${IMAGE_ROOTFS}/etc/environment
                ;;
            *rzg2l*)
                echo 'MOZ_GLXTEST_RESULT=VENDOR\\nARM\\nRENDERER\\nMali-G31\\nVERSION\\nOpenGL ES 3.2\\nTFP\\nTRUE\\nSCREEN_INFO\\n1920x1080:1;\\nTEST_TYPE\\nEGL' >> ${IMAGE_ROOTFS}/etc/environment
                echo 'MOZ_OMX_RZG2L=1' >> ${IMAGE_ROOTFS}/etc/environment
                ;;
            *)
                echo 'MOZ_GLXTEST_RESULT=VENDOR\\nProprietary\\nRENDERER\\nHardware\\nVERSION\\nN/A\\nTFP\\nTRUE\\nSCREEN_INFO\\n1920x1080:1;\\nTEST_TYPE\\nEGL' >> ${IMAGE_ROOTFS}/etc/environment
                ;;
        esac
    fi
}
