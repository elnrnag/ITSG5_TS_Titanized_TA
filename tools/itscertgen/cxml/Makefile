######################################################################
##
##  Created by: Denis Filatov
##
##  Copyleft (c) 2008 - 2015
##  This code is provided under the CeCill-C license agreement.
######################################################################
PROJECTROOT  = ..
BUILDROOT    = ../build
PROJECT      = cxml
DEBUG        = yes
testdir      = tests

alibs        = $(PROJECT)
solibs       = $(PROJECT)

sources       := cxml.c cxml_dcmi.c cxml_decode.c cxml_encode.c cxml_entity.c cxmlst.c
headers       := cxml.h 
tests         := parser.c parser_st.c
distfiles     := tests/test.xml tests/test_st.xml

cflags-POSIX  := -fPIC

include ../common.mk
