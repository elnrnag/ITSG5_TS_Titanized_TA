@echo off

REM Output path
REM Path where certificates must be stored
SET OUTPATH=certificates

REM IUT public key file
REM Set it for IUTs which store private keys internally
REM SET IUT_PUBLIC_KEY=<path>

REM Certificate generator path
REM Visual studio
SET CERTGEN=..\tools\itscertgen\build\msvc\Debug\itscertgen.exe 
REM MINGW32
REM SET CERTGEN=..\tools\itscertgen\build\mingw32-d\itscertgen.exe
REM ---------------------------------------------------------------

if DEFINED IUT_PUBLIC_KEY (
	IF NOT EXIST %IUT_PUBLIC_KEY% (^
		echo %IUT_PUBLIC_KEY%: IUT public key file not found 
		exit 1
	)
)

if not exist %OUTPATH% md %OUTPATH%

REM Generate Generic TS certificates
for  %%f in ( profiles\CERT_*_ROOT.xml ^
	profiles\CERT_TS_?_EA.xml profiles\CERT_TS_??_EA.xml ^
	profiles\CERT_TS_?_AA.xml profiles\CERT_TS_??_AA.xml ^
	profiles\CERT_TS_?_EC.xml profiles\CERT_TS_??_EC.xml ^
	profiles\CERT_TS_?_AT.xml profiles\CERT_TS_??_AT.xml  ) DO (
	echo %%f:
	%CERTGEN% -C gencerts.cfg -o %OUTPATH% %%f
)

REM Generate various valid or invalid TS certificates
for  %%f in ( profiles\CERT_*_BO_CA.xml profiles\CERT_*_BV_CA.xml ^
	profiles\CERT_*_BO_AA.xml profiles\CERT_*_BV_AA.xml ^
	profiles\CERT_*_BO_EA.xml profiles\CERT_*_BV_EA.xml ^
	profiles\CERT_*_BO_EC.xml profiles\CERT_*_BV_EC.xml ^
	profiles\CERT_*_BO_AT.xml profiles\CERT_*_BV_AT.xml ) DO (
	echo %%f:
	%CERTGEN% -C gencerts.cfg -o %OUTPATH% %%f
)

REM Generate IUT certificates
for  %%f in (profiles\CERT_IUT_*.xml) DO (
	echo %%f:
        if DEFINED IUT_PUBLIC_KEY (
		%CERTGEN% -C gencerts.cfg -o %OUTPATH% -v %IUT_PUBLIC_KEY% %%f
	) ELSE (
		%CERTGEN% -C gencerts.cfg -o %OUTPATH% %%f
	)
)
