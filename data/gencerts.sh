#!/bin/sh

# Output path
# Path where certificates must be stored
OUTPATH=certificates

# IUT public key file
# Set it for IUTs which store private keys internally
# IUT_PUBLIC_KEY=<path>

# Certificate generator path
CERTGEN=../tools/itscertgen/build/itscertgen
#CERTGEN=../tools/itscertgen/build/msvc/Debug/itscertgen.exe 
# ---------------------------------------------------------------

if ! [ -x "$CERTGEN" ]; then 
  echo "$CERTGEN: generator not found"
  exit 1
fi

IUT_PARAMS=
if [ "x" !=  "x$IUT_PUBLIC_KEY" ]; then
	 [ -f "$IUT_PUBLIC_KEY" ] || ( echo "$IUT_PUBLIC_KEY: public key file not found" ; exit 1 )
	 IUT_PARAMS="-v \"$IUT_PUBLIC_KEY\""
fi

mkdir -p "$OUTPATH"

function generate()
{
	echo "$1:"
	"${CERTGEN}" -C gencerts.cfg -o "$OUTPATH" $IUT_PARAMS "$1" || exit 1
}

# Generate Generic Root certificates
ls -1 profiles/CERT_*_ROOT.xml 2>/dev/null | while read F; do generate "$F"; done

# Generate Generic authority certificates
ls -1	profiles/CERT_TS_?_CA.xml profiles/CERT_TS_??_CA.xml \
	profiles/CERT_TS_?_EA.xml profiles/CERT_TS_??_EA.xml \
	profiles/CERT_TS_?_AA.xml profiles/CERT_TS_??_AA.xml 2>/dev/null | while read F; do generate "$F"; done

# Generate Generic authorization tickets
ls -1	profiles/CERT_TS_?_AT.xml profiles/CERT_TS_??_AT.xml 2>/dev/null | while read F; do generate "$F"; done

# Generate various valid or invalid authority certificates
ls -1	profiles/CERT_*_BO_CA.xml profiles/CERT_*_BV_CA.xml 2>/dev/null | while read F; do generate "$F"; done
ls -1	profiles/CERT_*_BO_AA.xml profiles/CERT_*_BV_AA.xml 2>/dev/null | while read F; do generate "$F"; done
ls -1	profiles/CERT_*_BO_EA.xml profiles/CERT_*_BV_EA.xml 2>/dev/null | while read F; do generate "$F"; done
ls -1	profiles/CERT_*_BO_EC.xml profiles/CERT_*_BV_EC.xml 2>/dev/null | while read F; do generate "$F"; done
ls -1	profiles/CERT_*_BO_AT.xml profiles/CERT_*_BV_AT.xml 2>/dev/null | while read F; do generate "$F"; done

# Generate IUT certificates
[ -n "$IUT_PUBLIC_KEY" ] && IUT_PARAMS="-v \"$IUT_PUBLIC_KEY\""
ls -1 profiles/CERT_IUT_*.xml 2>/dev/null | while read F; do generate "$F" ; done
