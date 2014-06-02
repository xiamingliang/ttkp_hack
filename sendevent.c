#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdint.h>
#include <fcntl.h>
#include <sys/ioctl.h>
//#include <linux/input.h> // this does not compile
#include <errno.h>


// from <linux/input.h>

struct input_event {
	struct timeval time;
	__u16 type;
	__u16 code;
	__s32 value;
};

#define EVIOCGVERSION		_IOR('E', 0x01, int)			/* get driver version */
#define EVIOCGID		_IOR('E', 0x02, struct input_id)	/* get device ID */
#define EVIOCGKEYCODE		_IOR('E', 0x04, int[2])			/* get keycode */
#define EVIOCSKEYCODE		_IOW('E', 0x04, int[2])			/* set keycode */

#define EVIOCGNAME(len)		_IOC(_IOC_READ, 'E', 0x06, len)		/* get device name */
#define EVIOCGPHYS(len)		_IOC(_IOC_READ, 'E', 0x07, len)		/* get physical location */
#define EVIOCGUNIQ(len)		_IOC(_IOC_READ, 'E', 0x08, len)		/* get unique identifier */

#define EVIOCGKEY(len)		_IOC(_IOC_READ, 'E', 0x18, len)		/* get global keystate */
#define EVIOCGLED(len)		_IOC(_IOC_READ, 'E', 0x19, len)		/* get all LEDs */
#define EVIOCGSND(len)		_IOC(_IOC_READ, 'E', 0x1a, len)		/* get all sounds status */
#define EVIOCGSW(len)		_IOC(_IOC_READ, 'E', 0x1b, len)		/* get all switch states */

#define EVIOCGBIT(ev,len)	_IOC(_IOC_READ, 'E', 0x20 + ev, len)	/* get event bits */
#define EVIOCGABS(abs)		_IOR('E', 0x40 + abs, struct input_absinfo)		/* get abs value/limits */
#define EVIOCSABS(abs)		_IOW('E', 0xc0 + abs, struct input_absinfo)		/* set abs value/limits */

#define EVIOCSFF		_IOC(_IOC_WRITE, 'E', 0x80, sizeof(struct ff_effect))	/* send a force effect to a force feedback device */
#define EVIOCRMFF		_IOW('E', 0x81, int)			/* Erase a force effect */
#define EVIOCGEFFECTS		_IOR('E', 0x84, int)			/* Report number of effects playable at the same time */

#define EVIOCGRAB		_IOW('E', 0x90, int)			/* Grab/Release device */

// end <linux/input.h>



int sendevent_main(int argc, char *argv[])
{
    int i, count;
    int fd;
    int ret;
    int version;
    struct input_event event[10] = {
        {
            .type = 1,
            .code = 330,
            .value= 1
        },
        {
            .type = 3,
            .code = 48,
            .value= 20,
        },
        {
            .type = 3  ,
            .code = 53,
            .value= 502,
        },
        {
            .type = 3  ,
            .code = 54,
            .value= 441,
        },
        {
            .type = 3  ,
            .code = 57,
            .value= 0,
        },
        {
            .type = 0  ,
            .code = 2,
            .value= 0,
        },
        {
            .type = 0  ,
            .code = 0,
            .value= 0,
        },
        {
            .type = 1,
            .code = 330,
            .value= 1,
        },
        {
            .type = 0,
            .code = 2,
            .value= 0,
        },
        {
            .type = 0,
            .code = 0,
            .value= 0,
        }
    };

    if(argc != 5) {
        fprintf(stderr, "use: %s device type code value\n", argv[0]);
        return 1;
    }

    fd = open(argv[1], O_RDWR);
    if(fd < 0) {
        fprintf(stderr, "could not open %s, %s\n", argv[optind], strerror(errno));
        return 1;
    }
    if (ioctl(fd, EVIOCGVERSION, &version)) {
        fprintf(stderr, "could not get driver version for %s, %s\n", argv[optind], strerror(errno));
        return 1;
    }
    count = 0;
    while (count++ < 500) {
        for (i = 0; i < 10; i++) {
            //memset(&event[i], 0, sizeof(struct input_event));

            /*
               event[0].type = 1;
               event[0].code = atoi(argv[3]);
               event[0].value = atoi(argv[4]);
             */
            ret = write(fd, &event[i], sizeof(struct input_event));
            if(ret < sizeof(struct input_event)) {
                fprintf(stderr, "write event failed, %s\n", strerror(errno));
                return -1;
            }
        }
    }
    return 0;
}
/*
while true; 
do
    sendevent /dev/input/event3 1 330 1
    sendevent /dev/input/event3 3 48 20
    sendevent /dev/input/event3 3 53 502
    sendevent /dev/input/event3 3 54 441
    sendevent /dev/input/event3 3 57 0
    sendevent /dev/input/event3 0 0002 0
    sendevent /dev/input/event3 0 0000 0
    sendevent /dev/input/event3 1 330 0
    sendevent /dev/input/event3 0 2 0
    sendevent /dev/input/event3 0 0 0
done
*/
