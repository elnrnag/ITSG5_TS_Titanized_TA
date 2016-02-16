/**
 * @author      ETSI / STF481 / Yann Garcia
 * @version     $URL: file:///D:/RepositoriesNew/ITS/trunk/javasrc/codec/org/etsi/ttcn/codec/its/security/Plugin.java $
 *              $Id: Plugin.java 2230 2015-06-03 09:11:02Z mullers $
 */
package org.etsi.ttcn.codec.its.security;

import org.etsi.ttcn.codec.CodecFactory;
import org.etsi.ttcn.codec.generic.Union;
import org.etsi.ttcn.tci.TciTypeClass;

public class Plugin {

    public static void init() {
        CodecFactory cf = CodecFactory.getInstance();
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "AcEnableSecurity", AcEnableSecurity.class);
        // Draft ETSI TS 103 097 V1.1.14 Clause 4.2    Specification of basic format elements
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "PublicKey", PublicKey.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "EccPoint", EccPoint.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "AesCcm", AesCcm.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "SignerInfo", SignerInfo.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "Signature", Signature.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "EcdsaSignature", EcdsaSignature.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "Time64WithStandardDeviation", Time64WithStandardDeviation.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "Duration", Duration.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "TwoDLocation", TwoDLocation.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "ThreeDLocation", ThreeDLocation.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "EncryptionParameters", EncryptionParameters.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "GeographicRegion", GeographicRegion.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "CircularRegion", CircularRegion.class);
        
        cf.setCodec(TciTypeClass.UNION, "LibItsSecurity", "SignerInfoContainer", SignerInfoContainer.class);
        cf.setCodec(TciTypeClass.UNION, "LibItsSecurity", "EncryptionParametersContainer", EncryptionParametersContainer.class);
        cf.setCodec(TciTypeClass.UNION, "LibItsSecurity", "GeographicRegionContainer", GeographicRegionContainer.class);
        
        cf.setCodec(TciTypeClass.ENUMERATED, "LibItsSecurity", "RegionDictionary", RegionDictionary.class);
        
        cf.setCodec(TciTypeClass.INTEGER, "LibItsSecurity", "Time32", Time32.class);
        cf.setCodec(TciTypeClass.INTEGER, "LibItsSecurity", "Time64", Time64.class);
        cf.setCodec(TciTypeClass.INTEGER, "LibItsSecurity", "IntX", IntX.class);
        
        // Draft ETSI TS 103 097 V1.1.14 Clause 5    Specification of security header
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "SecuredMessage", SecuredMessage.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "HeaderField", HeaderField.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "SecPayload", SecPayload.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "TrailerField", TrailerField.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "RecipientInfo", RecipientInfo.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "EciesEncryptedKey", EciesEncryptedKey.class);
        
        cf.setCodec(TciTypeClass.SET_OF, "LibItsSecurity", "HeaderFields", RecordOf.class);
        cf.setCodec(TciTypeClass.SET_OF, "LibItsSecurity", "IntXs", RecordOf.class);
        cf.setCodec(TciTypeClass.SET_OF, "LibItsSecurity", "HashedId3s", RecordOf.class);
        cf.setCodec(TciTypeClass.SET_OF, "LibItsSecurity", "RecipientInfos", RecordOf.class);
        cf.setCodec(TciTypeClass.SET_OF, "LibItsSecurity", "TrailerFields", RecordOf.class);
        cf.setCodec(TciTypeClass.SET_OF, "LibItsSecurity", "RectangularRegions", RecordOf.class);
        cf.setCodec(TciTypeClass.SET_OF, "LibItsSecurity", "PolygonalRegion", RecordOf.class);
        // Change record of into set of in order to use superset
        // record of shall be used, refer to ETSI TS 103 097 V 1.1.6 (2014-05)
        cf.setCodec(TciTypeClass.SET_OF, "LibItsSecurity", "HeaderFields", RecordOf.class);
        
        cf.setCodec(TciTypeClass.UNION, "LibItsSecurity", "PublicKeyContainer", Union.class);
        cf.setCodec(TciTypeClass.UNION, "LibItsSecurity", "EccPointContainer", Union.class);
        cf.setCodec(TciTypeClass.UNION, "LibItsSecurity", "SignatureContainer", Union.class);
        
        cf.setCodec(TciTypeClass.UNION, "LibItsSecurity", "HeaderFieldContainer", HeaderFieldContainer.class);
        cf.setCodec(TciTypeClass.UNION, "LibItsSecurity", "RecipientInfoContainer", RecipientInfoContainer.class);
        cf.setCodec(TciTypeClass.UNION, "LibItsSecurity", "TrailerFieldContainer", TrailerFieldContainer.class);
        
        // Draft ETSI TS 103 097 V1.1.14 Clause 6    Specification of certificate format
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "ToBeSignedCertificate", ToBeSignedCertificate.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "Certificate", Certificate.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "SubjectInfo", SubjectInfo.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "SubjectAssurance", SubjectAssurance.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "SubjectAttribute", SubjectAttribute.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "ValidityRestriction", ValidityRestriction.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "ItsAidSsp", ItsAidSsp.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "ServiceSpecificPermissions", ServiceSpecificPermissions.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "SspCAM", SspCAM.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "SspDENM", SspDENM.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "CertificateWithAlgo", CertificateWithAlgo.class);
        
        cf.setCodec(TciTypeClass.UNION, "LibItsSecurity", "SubjectAttributeContainer", SubjectAttributeContainer.class);
        cf.setCodec(TciTypeClass.UNION, "LibItsSecurity", "ValidityRestrictionContainer", ValidityRestrictionContainer.class);
        
        cf.setCodec(TciTypeClass.SET_OF, "LibItsSecurity", "CertificateChain", RecordOf.class);
        cf.setCodec(TciTypeClass.SET_OF, "LibItsSecurity", "ValidityRestrictions", RecordOf.class);
        cf.setCodec(TciTypeClass.SET_OF, "LibItsSecurity", "SubjectAttributes", RecordOf.class);
        cf.setCodec(TciTypeClass.SET_OF, "LibItsSecurity", "ItsAidSsps", RecordOf.class);
        // Change record of into set of in order to use superset
        // record of shall be used, refer to ETSI TS 103 097 V 1.1.6 (2014-05)
        
        // TCT3 specific
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "PublicKeyContainer.eccPoint", EccPoint.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "SignatureContainer.ecdsa_signature", EcdsaSignature.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "PublicKeyContainer.aesCcm", Signature.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "HeaderFieldContainer.signer", SignerInfo.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "EncryptionParametersContainer.public_key", EncryptionParameters.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "GeographicRegionContainer.circular_region", CircularRegion.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "GeographicRegionContainer.id_region", IdentifiedRegion.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "HeaderFieldContainer.generation_time_with_standard_deviation", HeaderField.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "HeaderFieldContainer.generation_location", HeaderField.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "HeaderFieldContainer.enc_params", EncryptionParameters.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "RecipientInfoContainer.enc_key", EciesEncryptedKey.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "RecipientInfoContainer.enc_key_other", RecipientInfoContainer.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "TrailerFieldContainer.signature_", Signature.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "TrailerFieldContainer.security_field", TrailerFieldContainer.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "SubjectAttributeContainer.key", PublicKey.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "SubjectAttributeContainer.rv", EccPoint.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "SubjectAttributeContainer.assurance_level", SubjectAssurance.class);
        cf.setCodec(TciTypeClass.RECORD, "LibItsSecurity", "ValidityRestrictionContainer.region", GeographicRegion.class);
        
        cf.setCodec(TciTypeClass.INTEGER, "LibItsSecurity", "HeaderFieldContainer.generation_time", Time64.class);
        cf.setCodec(TciTypeClass.INTEGER, "LibItsSecurity", "HeaderFieldContainer.expiry_time", Time32.class);
        cf.setCodec(TciTypeClass.INTEGER, "LibItsSecurity", "HeaderFieldContainer.its_aid", IntX.class);
        cf.setCodec(TciTypeClass.INTEGER, "LibItsSecurity", "ValidityRestrictionContainer.end_validity", Time32.class);
        
        cf.setCodec(TciTypeClass.SET_OF, "LibItsSecurity", "SignerInfoContainer.certificates", RecordOf.class);
        cf.setCodec(TciTypeClass.SET_OF, "LibItsSecurity", "GeographicRegionContainer.rectangular_region", RecordOf.class);
        cf.setCodec(TciTypeClass.SET_OF, "LibItsSecurity", "GeographicRegionContainer.polygonal_region", RecordOf.class);
        cf.setCodec(TciTypeClass.SET_OF, "LibItsSecurity", "HeaderFieldContainer.digests", RecordOf.class);
        cf.setCodec(TciTypeClass.SET_OF, "LibItsSecurity", "HeaderFieldContainer.recipients", RecordOf.class);
        cf.setCodec(TciTypeClass.SET_OF, "LibItsSecurity", "SubjectAttributeContainer.its_aid_list", RecordOf.class);
        cf.setCodec(TciTypeClass.SET_OF, "LibItsSecurity", "SubjectAttributeContainer.its_aid_ssp_list", RecordOf.class);
        // Change record of into set of in order to use superset
        // record of shall be used, refer to ETSI TS 103 097 V 1.1.6 (2014-05)
        
    }
} // End of class Plugin