#!/usr/bin/env bash

apt-get -y upgrade
apt-get -y update
apt-get -y install git python-pip unzip default-jdk build-essential debootstrap
apt-get -y insstall libjpeg62 libtiff5
cd /tmp
wget https://github.com/singularityware/singularity/releases/download/2.3.2/singularity-2.3.2.tar.gz
tar xvf singularity-2.3.2.tar.gz
cd singularity-2.3.2
./configure --prefix=/usr/local
make
make install
/bin/rm -rf /tmp/singularity*

cd /tmp
/usr/bin/wget http://bio3d.colorado.edu/imod/AMD64-RHEL5/imod_4.9.4_RHEL7-64_CUDA6.5.sh
/bin/chmod a+x imod_4.9.4_RHEL7-64_CUDA6.5.sh
/bin/sh imod_4.9.4_RHEL7-64_CUDA6.5.sh -yes
/bin/rm imod_4.9.4_RHEL7-64_CUDA6.5.sh

