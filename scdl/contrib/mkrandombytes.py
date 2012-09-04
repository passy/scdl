#!/usr/bin/env python

import os


def get_random_byte():
    return str(127 - ord(os.urandom(1)))


def main():
    print("return {" + ", ".join([get_random_byte() for i in range(20)]) + "};")


if __name__ == "__main__":
    main()
