outdir=../certificates/plu
installdir=C:\Users\filatov\Documents\Dropbox\Yann\certificates\PLU
distzip=$(outdir)/plu_ts_certificates.zip

# Do not forget to point it to the proper IUT public key 
# in order to generate IUT certificates using existing key pairs.
#IUT_PUBLIC_KEY := IUT.key

depdir=.deps
keydir=$(outdir)
GENOPTS := -C plu.cfg

itscertgen="../itscertgen.exe"
profiles        := $(wildcard CERT_*.xml)
profiles_iut_at := $(wildcard CERT_IUT_*AT.xml)
profiles_other  := $(filter-out $(profiles_iut_at), $(profiles))

certificates_iut_at := $(patsubst %.xml, $(outdir)/%.crt, $(profiles_iut_at))
certificates_other  := $(patsubst %.xml, $(outdir)/%.crt, $(profiles_other))
certificates        := $(certificates_iut_at) $(certificates_other)

depends      := $(patsubst %.xml, $(depdir)/%.d, $(profiles))
dirs         := $(sort $(outdir) $(keydir) $(depdir))

distfiles    := $(certificates_other) \
                $(wildcard $(patsubst %.crt, %.vkey, $(filter %_AT.crt %_EC.crt, $(certificates_other)))) \
                $(wildcard $(patsubst %.crt, %.ekey, $(filter %_AT.crt %_EC.crt, $(certificates_other))))

vendor_certs := $(wildcard vendors/*.zip)
vendors      := $(foreach z, $(patsubst vendors/%,%,$(vendor_certs)), $(firstword $(subst _, ,$(z))))
vendor_dirs  := $(addprefix $(outdir)/, $(vendors))


ifneq ($(sort $(IUT_PUBLIC_KEY)),)
  AT_GENOPTS = $(GENOPTS) -v $(IUT_PUBLIC_KEY)
else
  AT_GENOPTS = $(GENOPTS)
endif

all: $(dirs) $(certificates_iut_at) $(certificates_other) 

install: all $(installdir) $(installs)
$(installs): $(installdir)/% : $(outdir)/%
	cp -f $< $@
	
$(dirs) $(installdir): %:
	mkdir -p $@

echo:
	echo $(vendors)
clean:
	rm -f $(outdir)/* $(keydir)/* $(depdir)/*

dist: all vendors $(distzip)

vendors: $(vendor_dirs)
$(vendor_dirs): $(outdir)/%: 
	mkdir -p $@
	-rm -rf $@/*
	echo $<
	unzip -uo vendors/$**.zip -d $@

$(distzip): $(distfiles)
	zip -o -j $@ $^

$(certificates_other): $(outdir)/%.crt : %.xml
	$(itscertgen) -o $(outdir) $(GENOPTS) $<

$(certificates_iut_at): $(outdir)/%.crt : %.xml
	$(itscertgen) -o $(outdir) $(AT_GENOPTS) $<

$(depends): $(depdir)/%.d : %.xml
	@echo "Create $*.d"
	@mkdir -p $(depdir)
	@echo -n "$(outdir)/$*.crt: " > $@
	@cat $< | grep '<signer.*digest' | sed -e 's|^.*name="\([^"]*\)".*|$(outdir)/\1.crt|g' >> $@

include $(depends)
