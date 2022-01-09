package com.mcstalker.networking.objects;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

public class Filters {

	public static final HashMap<String, MinecraftVersion> availableMojangVersions = new HashMap<>();

	public static void setAvailableMojangVersions(JSONArray input) {
		input.forEach(obj -> {
			JSONObject json = (JSONObject) obj;
			String name = json.getString("version");
			int protocol = json.getInt("protocol");
			availableMojangVersions.put(name, new MinecraftVersion(protocol, name));
		});
	}

	public record MinecraftVersion(int protocolId, String name) implements Remappable<Integer> {
		public boolean isMajorRelease() {
			return name.split("\\.").length == 2;
		}

		@Override
		public Integer getRemapped() {
			return protocolId;
		}
	}

	public static class TypeAdapter extends com.google.gson.TypeAdapter<Remappable<?>> {
		@Override
		public void write(JsonWriter jsonWriter, Remappable remappable) throws IOException {
			if (remappable.getRemapped() instanceof String s) {
				jsonWriter.value(s);
			} else if (remappable.getRemapped() instanceof Integer i) {
				jsonWriter.value(i);
			} else if (remappable.getRemapped() instanceof Boolean b) {
				jsonWriter.value(b);
			} else {
				jsonWriter.value(remappable.getRemapped().toString());
			}
		}

		@Override
		public Remappable<?> read(JsonReader jsonReader) {
			return null;
		}
	}

	public interface Remappable<T> {
		 T getRemapped();
	}

	public interface FancyName {
		String getFancyName();
	}

	public enum AscDesc implements Remappable<String>, FancyName {
		ASC("Ascending"),
		DESC("Descending");

		private final String fancyName;

		AscDesc(String fancyName) {
			this.fancyName = fancyName;
		}

		@Override
		public String getFancyName() {
			return fancyName.toUpperCase(Locale.ENGLISH);
		}

		@Override
		public String getRemapped() {
			return this.name();
		}
	}

	public enum AuthStatus implements Remappable<Integer> {
		ANY(0),
		CRACKED(1),
		ONLINE(2);

		private final int id;

		AuthStatus(int id) {
			this.id = id;
		}

		@Override
		public Integer getRemapped() {
			return this.id;
		}
	}

	public enum WhiteListStatus implements Remappable<Integer> {
		ANY(0),
		ON(1),
		OFF(2);

		private final int id;

		WhiteListStatus(int id) {
			this.id = id;
		}

		@Override
		public Integer getRemapped() {
			return this.id;
		}
	}

	public enum SortMode implements Remappable<String> {
		UPDATED,
		EMPTY,
		TOP;

		public String getRemapped() {
			return this.name().toLowerCase(Locale.ENGLISH);
		}
	}

	public enum Country implements Remappable<String> {
		ALL("All"),
		AF("Afghanistan (AF)"),
		AX("Åland Islands (AX)"),
		AL("Albania (AL)"),
		DZ("Algeria (DZ)"),
		AS("American Samoa (AS)"),
		AD("Andorra (AD)"),
		AO("Angola (AO)"),
		AI("Anguilla (AI)"),
		AQ("Antarctica (AQ)"),
		AG("Antigua and Barbuda (AG)"),
		AR("Argentina (AR)"),
		AM("Armenia (AM)"),
		AW("Aruba (AW)"),
		AU("Australia (AU)"),
		AT("Austria (AT)"),
		AZ("Azerbaijan (AZ)"),
		BS("Bahamas (BS)"),
		BH("Bahrain (BH)"),
		BD("Bangladesh (BD)"),
		BB("Barbados (BB)"),
		BY("Belarus (BY)"),
		BE("Belgium (BE)"),
		BZ("Belize (BZ)"),
		BJ("Benin (BJ)"),
		BM("Bermuda (BM)"),
		BT("Bhutan (BT)"),
		BO("Bolivia (BO)"),
		BA("Bosnia and Herzegovina (BA)"),
		BW("Botswana (BW)"),
		BV("Bouvet Island (BV)"),
		BR("Brazil (BR)"),
		IO("British Indian Ocean Territory (IO)"),
		BN("Brunei Darussalam (BN)"),
		BG("Bulgaria (BG)"),
		BF("Burkina Faso (BF)"),
		BI("Burundi (BI)"),
		KH("Cambodia (KH)"),
		CM("Cameroon (CM)"),
		CA("Canada (CA)"),
		CV("Cape Verde (CV)"),
		KY("Cayman Islands (KY)"),
		CF("Central African Republic (CF)"),
		TD("Chad (TD)"),
		CL("Chile (CL)"),
		CN("China (CN)"),
		CX("Christmas Island (CX)"),
		CC("Cocos (Keeling) Islands (CC)"),
		CO("Colombia (CO)"),
		KM("Comoros (KM)"),
		CG("Congo (CG)"),
		CD("Congo, The Democratic Republic of the (CD)"),
		CK("Cook Islands (CK)"),
		CR("Costa Rica (CR)"),
		CI("Cote DIvoire (CI)"),
		HR("Croatia (HR)"),
		CU("Cuba (CU)"),
		CY("Cyprus (CY)"),
		CZ("Czech Republic (CZ)"),
		DK("Denmark (DK)"),
		DJ("Djibouti (DJ)"),
		DM("Dominica (DM)"),
		DO("Dominican Republic (DO)"),
		EC("Ecuador (EC)"),
		EG("Egypt (EG)"),
		SV("El Salvador (SV)"),
		GQ("Equatorial Guinea (GQ)"),
		ER("Eritrea (ER)"),
		EE("Estonia (EE)"),
		ET("Ethiopia (ET)"),
		FK("Falkland Islands (Malvinas) (FK)"),
		FO("Faroe Islands (FO)"),
		FJ("Fiji (FJ)"),
		FI("Finland (FI)"),
		FR("France (FR)"),
		GF("French Guiana (GF)"),
		PF("French Polynesia (PF)"),
		TF("French Southern Territories (TF)"),
		GA("Gabon (GA)"),
		GM("Gambia (GM)"),
		GE("Georgia (GE)"),
		DE("Germany (DE)"),
		GH("Ghana (GH)"),
		GI("Gibraltar (GI)"),
		GR("Greece (GR)"),
		GL("Greenland (GL)"),
		GD("Grenada (GD)"),
		GP("Guadeloupe (GP)"),
		GU("Guam (GU)"),
		GT("Guatemala (GT)"),
		GG("Guernsey (GG)"),
		GN("Guinea (GN)"),
		GW("Guinea-Bissau (GW)"),
		GY("Guyana (GY)"),
		HT("Haiti (HT)"),
		HM("Heard Island and Mcdonald Islands (HM)"),
		VA("Holy See (Vatican City State) (VA)"),
		HN("Honduras (HN)"),
		HK("Hong Kong (HK)"),
		HU("Hungary (HU)"),
		IS("Iceland (IS)"),
		IN("India (IN)"),
		ID("Indonesia (ID)"),
		IR("Iran, Islamic Republic Of (IR)"),
		IQ("Iraq (IQ)"),
		IE("Ireland (IE)"),
		IM("Isle of Man (IM)"),
		IL("Israel (IL)"),
		IT("Italy (IT)"),
		JM("Jamaica (JM)"),
		JP("Japan (JP)"),
		JE("Jersey (JE)"),
		JO("Jordan (JO)"),
		KZ("Kazakhstan (KZ)"),
		KE("Kenya (KE)"),
		KI("Kiribati (KI)"),
		KP("Korea, Democratic PeopleS Republic of (KP)"),
		KR("Korea, Republic of (KR)"),
		KW("Kuwait (KW)"),
		KG("Kyrgyzstan (KG)"),
		LA("Lao PeopleS Democratic Republic (LA)"),
		LV("Latvia (LV)"),
		LB("Lebanon (LB)"),
		LS("Lesotho (LS)"),
		LR("Liberia (LR)"),
		LY("Libyan Arab Jamahiriya (LY)"),
		LI("Liechtenstein (LI)"),
		LT("Lithuania (LT)"),
		LU("Luxembourg (LU)"),
		MO("Macao (MO)"),
		MK("Macedonia, The Former Yugoslav Republic of (MK)"),
		MG("Madagascar (MG)"),
		MW("Malawi (MW)"),
		MY("Malaysia (MY)"),
		MV("Maldives (MV)"),
		ML("Mali (ML)"),
		MT("Malta (MT)"),
		MH("Marshall Islands (MH)"),
		MQ("Martinique (MQ)"),
		MR("Mauritania (MR)"),
		MU("Mauritius (MU)"),
		YT("Mayotte (YT)"),
		MX("Mexico (MX)"),
		FM("Micronesia, Federated States of (FM)"),
		MD("Moldova, Republic of (MD)"),
		MC("Monaco (MC)"),
		MN("Mongolia (MN)"),
		MS("Montserrat (MS)"),
		MA("Morocco (MA)"),
		MZ("Mozambique (MZ)"),
		MM("Myanmar (MM)"),
		NA("Namibia (NA)"),
		NR("Nauru (NR)"),
		NP("Nepal (NP)"),
		NL("Netherlands (NL)"),
		AN("Netherlands Antilles (AN)"),
		NC("New Caledonia (NC)"),
		NZ("New Zealand (NZ)"),
		NI("Nicaragua (NI)"),
		NE("Niger (NE)"),
		NG("Nigeria (NG)"),
		NU("Niue (NU)"),
		NF("Norfolk Island (NF)"),
		MP("Northern Mariana Islands (MP)"),
		NO("Norway (NO)"),
		OM("Oman (OM)"),
		PK("Pakistan (PK)"),
		PW("Palau (PW)"),
		PS("Palestinian Territory, Occupied (PS)"),
		PA("Panama (PA)"),
		PG("Papua New Guinea (PG)"),
		PY("Paraguay (PY)"),
		PE("Peru (PE)"),
		PH("Philippines (PH)"),
		PN("Pitcairn (PN)"),
		PL("Poland (PL)"),
		PT("Portugal (PT)"),
		PR("Puerto Rico (PR)"),
		QA("Qatar (QA)"),
		RE("Reunion (RE)"),
		RO("Romania (RO)"),
		RU("Russian Federation (RU)"),
		RW("RWANDA (RW)"),
		SH("Saint Helena (SH)"),
		KN("Saint Kitts and Nevis (KN)"),
		LC("Saint Lucia (LC)"),
		PM("Saint Pierre and Miquelon (PM)"),
		VC("Saint Vincent and the Grenadines (VC)"),
		WS("Samoa (WS)"),
		SM("San Marino (SM)"),
		ST("Sao Tome and Principe (ST)"),
		SA("Saudi Arabia (SA)"),
		SN("Senegal (SN)"),
		CS("Serbia and Montenegro (CS)"),
		SC("Seychelles (SC)"),
		SL("Sierra Leone (SL)"),
		SG("Singapore (SG)"),
		SK("Slovakia (SK)"),
		SI("Slovenia (SI)"),
		SB("Solomon Islands (SB)"),
		SO("Somalia (SO)"),
		ZA("South Africa (ZA)"),
		GS("South Georgia and the South Sandwich Islands (GS)"),
		ES("Spain (ES)"),
		LK("Sri Lanka (LK)"),
		SD("Sudan (SD)"),
		SR("Suriname (SR)"),
		SJ("Svalbard and Jan Mayen (SJ)"),
		SZ("Swaziland (SZ)"),
		SE("Sweden (SE)"),
		CH("Switzerland (CH)"),
		SY("Syrian Arab Republic (SY)"),
		TW("Taiwan, Province of China (TW)"),
		TJ("Tajikistan (TJ)"),
		TZ("Tanzania, United Republic of (TZ)"),
		TH("Thailand (TH)"),
		TL("Timor-Leste (TL)"),
		TG("Togo (TG)"),
		TK("Tokelau (TK)"),
		TO("Tonga (TO)"),
		TT("Trinidad and Tobago (TT)"),
		TN("Tunisia (TN)"),
		TR("Turkey (TR)"),
		TM("Turkmenistan (TM)"),
		TC("Turks and Caicos Islands (TC)"),
		TV("Tuvalu (TV)"),
		UG("Uganda (UG)"),
		UA("Ukraine (UA)"),
		AE("United Arab Emirates (AE)"),
		GB("United Kingdom (GB)"),
		US("United States (US)"),
		UM("United States Minor Outlying Islands (UM)"),
		UY("Uruguay (UY)"),
		UZ("Uzbekistan (UZ)"),
		VU("Vanuatu (VU)"),
		VE("Venezuela (VE)"),
		VN("Viet Nam (VN)"),
		VG("Virgin Islands, British (VG)"),
		VI("Virgin Islands, U.S. (VI)"),
		WF("Wallis and Futuna (WF)"),
		EH("Western Sahara (EH)"),
		YE("Yemen (YE)"),
		ZM("Zambia (ZM)"),
		ZW("Zimbabwe (ZW)");

		Country(String fancyName) {
			this.fancyName = fancyName;
		}

		private final String fancyName;

		public String getFancyName() {
			return fancyName;
		}

		@Override
		public String getRemapped() {
			return this.name().toLowerCase(Locale.ENGLISH);
		}
	}
}