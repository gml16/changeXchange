package entity.changexchange.utils;

public enum Currency {

//    AED("AED"), AFN("AFN"), ALL("ALL"), AMD("AMD"), ANG("ANG"), AOA("AOA"),
//    ARS("ARS"), AUD("AUD"), AWG("AWG"), AZN("AZN"), BAM("BAM"), BBD("BBD"),
//    BDT("BDT"), BGN("BGN"), BHD("BHD"), BIF("BIF"), BMD("BMD"), BND("BND"),
//    BOB("BOB"), BRL("BRL"), BSD("BSD"), BTN("BTN"), BWP("BWP"), BYN("BYN"),
//    BZD("BZD"), CAD("CAD"), CDF("CDF"), CHF("CHF"), CLP("CLP"), CNY("CNY"),
//    COP("COP"), CRC("CRC"), CUC("CUC"), CUP("CUP"), CVE("CVE"), CZK("CZK"),
//    DJF("DJF"), DKK("DKK"), DOP("DOP"), DZD("DZD"), EGP("EGP"), ERN("ERN"),
//    ETB("ETB"), EUR("EUR"), FJD("FJD"), FKP("FKP"), GBP("GBP"), GEL("GEL"),
//    GGP("GGP"), GHS("GHS"), GIP("GIP"), GMD("GMD"), GNF("GNF"), GTQ("GTQ"),
//    GYD("GYD"), HKD("HKD"), HNL("HNL"), HRK("HRK"), HTG("HTG"), HUF("HUF"),
//    IDR("IDR"), ILS("ILS"), IMP("IMP"), INR("INR"), IQD("IQD"), IRR("IRR"),
//    ISK("ISK"), JEP("JEP"), JMD("JMD"), JOD("JOD"), JPY("JPY"), KES("KES"),
//    KGS("KGS"), KHR("KHR"), KMF("KMF"), KPW("KPW"), KRW("KRW"), KWD("KWD"),
//    KYD("KYD"), KZT("KZT"), LAK("LAK"), LBP("LBP"), LKR("LKR"), LRD("LRD"),
//    LSL("LSL"), LYD("LYD"), MAD("MAD"), MDL("MDL"), MGA("MGA"), MKD("MKD"),
//    MMK("MMK"), MNT("MNT"), MOP("MOP"), MRU("MRU"), MUR("MUR"), MVR("MVR"),
//    MWK("MWK"), MXN("MXN"), MYR("MYR"), MZN("MZN"), NAD("NAD"), NGN("NGN"),
//    NIO("NIO"), NOK("NOK"), NPR("NPR"), NZD("NZD"), OMR("OMR"), PEN("PEN"),
//    PGK("PGK"), PHP("PHP"), PKR("PKR"), PLN("PLN"), PYG("PYG"), QAR("QAR"),
//    RON("RON"), RSD("RSD"), RUB("RUB"), RWF("RWF"), SAR("SAR"), SBD("SBD"),
//    SCR("SCR"), SDG("SDG"), SEK("SEK"), SGD("SGD"), SHP("SHP"), SLL("SLL"),
//    SOS("SOS"), SPL("SPL"), SRD("SRD"), STN("STN"), SVC("SVC"), SYP("SYP"),
//    SZL("SZL"), THB("THB"), TJS("TJS"), TMT("TMT"), TND("TND"), TOP("TOP"),
//    TRY("TRY"), TTD("TTD"), TVD("TVD"), TWD("TWD"), TZS("TZS"), UAH("UAH"),
//    UGX("UGX"), USD("USD"), UYU("UYU"), UZS("UZS"), VEF("VEF"), VND("VND"),
//    VUV("VUV"), WST("WST"), XAF("XAF"), BEA("BEA"), EAC("EAC"), CFA("CFA"),
//    XCD("XCD"), XDR("XDR"), IMF("IMF"), XOF("XOF"), BCE("BCE"), CEA("CEA"),
//    EAO("EAO"), XPF("XPF"), CFP("CFP"), YER("YER"), ZAR("ZAR"), ZMW("ZMW"),
//    ZWD("ZWD");

    ARS("ARS"),
    AUD("AUD"),
    BRL("BRL"),
    CAD("CAD"),
    CHF("CHF"),
    CKK("CKK"),
    CLP("CLP"),
    COP("COP"),
    EUR("EUR"),
    GBP("GBP"),
    HKD("HKD"),
    IDR("IDR"),
    JPY("JPY"),
    KRW("KRW"),
    MXN("MXN"),
    NOK("NOK"),
    PEN("PEN"),
    SEK("SEK"),
    SGD("SGD"),
    USD("USD"),
    ZAR("ZAR");

    private String currency;

    Currency(String c) {
        currency = c;
    }

    @Override
    public String toString() {
        return currency;
    }

}
