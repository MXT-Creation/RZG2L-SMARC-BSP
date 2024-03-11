#include <fcntl.h>
#include <linux/uinput.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/signalfd.h>
#include <unistd.h>


int main(int argc, char *argv[])
{
	struct uinput_setup usetup;
	struct signalfd_siginfo fdsi;
	sigset_t mask;
	ssize_t s;
	int fd, sfd;
	int opt;
	int ret = 0;
	int oflag = 0;

	while ((opt = getopt(argc, argv, "ho")) != -1) {
		switch (opt) {
		case 'o':
			oflag = 1;
			break;
		case 'h':
		default:
			fprintf(stderr, "Usage: %s [-o]\n", argv[0]);
			exit(EXIT_FAILURE);
		}
	}

	if (optind != argc) {
		fprintf(stderr, "Unexpected argument\n");
		exit(EXIT_FAILURE);
	}

	fd = open("/dev/uinput", O_WRONLY | O_NONBLOCK);
	if (fd == -1) {
		perror("open(/dev/uinput)");
		exit(EXIT_FAILURE);
	}

	if (oflag) {
		/*
		 * oneshot mode:
		 * Create a dummy-input device then remove it immediately.
		 */
		sfd = -1;
	} else {
		sigemptyset(&mask);
		sigaddset(&mask, SIGINT);
		sigaddset(&mask, SIGQUIT);
		sigaddset(&mask, SIGTERM);

		if (sigprocmask(SIG_BLOCK, &mask, NULL) == -1) {
			perror("sigprocmask");
			exit(EXIT_FAILURE);
		}

		sfd = signalfd(-1, &mask, 0);
		if (sfd == -1) {
			perror("signalfd");
			exit(EXIT_FAILURE);
		}
	}

	/*
	 * The ioctls below will enable the device that is about to be
	 * created, to pass key events, in this case the space key.
	 */
	ioctl(fd, UI_SET_EVBIT, EV_KEY);
	ioctl(fd, UI_SET_KEYBIT, KEY_SPACE);

	memset(&usetup, 0, sizeof(usetup));
	usetup.id.bustype = BUS_USB;
	usetup.id.vendor = 0x1234; /* sample vendor */
	usetup.id.product = 0x5678; /* sample product */
	strcpy(usetup.name, "dummy-input device");

	ioctl(fd, UI_DEV_SETUP, &usetup);
	ioctl(fd, UI_DEV_CREATE);

	if (sfd >= 0) {
		for (;;) {
			s = read(sfd, &fdsi, sizeof(fdsi));
			if (s != sizeof(fdsi)) {
				perror("read");
				ret = EXIT_FAILURE;
				break;
			}

			if (fdsi.ssi_signo == SIGINT || fdsi.ssi_signo == SIGQUIT ||
			    fdsi.ssi_signo == SIGTERM) {
				break;
			}
		}
	}

	ioctl(fd, UI_DEV_DESTROY);
	close(fd);

	return ret;
}
