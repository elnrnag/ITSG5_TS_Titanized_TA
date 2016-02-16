######################################################################
##
##  Created by: Denis Filatov
##
##  Copyleft (c) 2015
##  This code is provided under the CeCill-C license agreement.
######################################################################
PROJECTROOT    = ..
BUILDROOT      = ../build
PROJECT        = itscertgen
DEBUG          = yes
bins           = itscertgen
sources       := certgen.c ecc_openssl.c mkgmtime.c
sources-WIN32 := applink.c
packages      += openssl cxml cshared
include ../common.mk
