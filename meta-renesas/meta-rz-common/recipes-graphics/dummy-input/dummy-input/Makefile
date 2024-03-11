DESTDIR ?=
PREFIX ?= /usr

OBJS = \
    dummy-input.o

CFLAGS ?= -O2 -Wall -Werror
LDFLAGS =

.PHONY: all clean

all: dummy-input

install:
	install -d ${DESTDIR}${PREFIX}/bin
	install -m 0755 dummy-input ${DESTDIR}${PREFIX}/bin

clean:
	rm -f dummy-input $(OBJS)

dummy-input: $(OBJS)
