package org.fiddlemc.fiddle.impl.packetmapping.component.translatable;

import org.jspecify.annotations.Nullable;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Provides some functionality for Minecraft locales.
 */
public final class MinecraftLocaleUtil {

    private MinecraftLocaleUtil() {
        throw new UnsupportedOperationException();
    }

    public static class KnownLocale {

        public final String lowerCaseLocale;
        public final @Nullable String languageGroup;

        private KnownLocale(String lowerCaseLocale, @Nullable String languageGroup) {
            this.lowerCaseLocale = lowerCaseLocale;
            this.languageGroup = languageGroup != null ? languageGroup : computeLanguageGroup(lowerCaseLocale);
        }

        private KnownLocale(String lowerCaseLocale) {
            this(lowerCaseLocale, null);
        }

    }

    /**
     * Based on the <a href="https://minecraft.wiki/w/Language">wiki page</a>.
     *
     * <p>
     * Some languages have a hard-coded language group.
     * This is merely an attempt at auto-completing missing translations,
     * with the understanding that some of the resulting translations
     * may be confusing, inaccurate or objectionable.
     * </p>
     */
    private static KnownLocale[] knownLocales = {
        new KnownLocale("af_za"),
        new KnownLocale("ar_sa"),
        new KnownLocale("ast_es"),
        new KnownLocale("az_az"),
        new KnownLocale("ba_ru"),
        new KnownLocale("bar", "de"),
        new KnownLocale("be_by"),
        new KnownLocale("be_latn"),
        new KnownLocale("bg_bg"),
        new KnownLocale("br_fr"),
        new KnownLocale("brb", "nl"),
        new KnownLocale("bs_ba"),
        new KnownLocale("ca_es"),
        new KnownLocale("cs_cz"),
        new KnownLocale("cy_gb"),
        new KnownLocale("da_dk"),
        new KnownLocale("de_at"),
        new KnownLocale("de_ch"),
        new KnownLocale("de_de"),
        new KnownLocale("el_gr"),
        new KnownLocale("en_au"),
        new KnownLocale("en_ca"),
        new KnownLocale("en_gb"),
        new KnownLocale("en_nz"),
        new KnownLocale("en_pt"),
        new KnownLocale("en_ud"),
        new KnownLocale("en_us"),
        new KnownLocale("enp", "en"),
        new KnownLocale("enws", "en"),
        new KnownLocale("eo_uy"),
        new KnownLocale("es_ar"),
        new KnownLocale("es_cl"),
        new KnownLocale("es_ec"),
        new KnownLocale("es_es"),
        new KnownLocale("es_mx"),
        new KnownLocale("es_uy"),
        new KnownLocale("es_ve"),
        new KnownLocale("esan", "es"),
        new KnownLocale("et_ee"),
        new KnownLocale("eu_es"),
        new KnownLocale("fa_ir"),
        new KnownLocale("fi_fi"),
        new KnownLocale("fil_ph"),
        new KnownLocale("fo_fo"),
        new KnownLocale("fr_ca"),
        new KnownLocale("fr_fr"),
        new KnownLocale("fra_de"),
        new KnownLocale("fur_it"),
        new KnownLocale("fy_nl"),
        new KnownLocale("ga_ie"),
        new KnownLocale("gd_gb"),
        new KnownLocale("gl_es"),
        new KnownLocale("hal_ua"),
        new KnownLocale("haw_us"),
        new KnownLocale("he_il"),
        new KnownLocale("hi_in"),
        new KnownLocale("hn_no"),
        new KnownLocale("hr_hr"),
        new KnownLocale("hu_hu"),
        new KnownLocale("hy_am"),
        new KnownLocale("id_id"),
        new KnownLocale("ig_ng"),
        new KnownLocale("io_en"),
        new KnownLocale("is_is"),
        new KnownLocale("isv", "sk"),
        new KnownLocale("it_it"),
        new KnownLocale("ja_jp"),
        new KnownLocale("jbo_en"),
        new KnownLocale("ka_ge"),
        new KnownLocale("kk_kz"),
        new KnownLocale("kn_in"),
        new KnownLocale("ko_kr"),
        new KnownLocale("ksh", "de"),
        new KnownLocale("kw_gb"),
        new KnownLocale("ky_kg"),
        new KnownLocale("la_la"),
        new KnownLocale("lb_lu"),
        new KnownLocale("li_li"),
        new KnownLocale("lmo", "it"),
        new KnownLocale("lo_la"),
        new KnownLocale("lol_us", "en"),
        new KnownLocale("lt_lt"),
        new KnownLocale("lv_lv"),
        new KnownLocale("lzh", "zh"),
        new KnownLocale("mk_mk"),
        new KnownLocale("mn_mn"),
        new KnownLocale("ms_my"),
        new KnownLocale("mt_mt"),
        new KnownLocale("nah", "es"),
        new KnownLocale("nds_de"),
        new KnownLocale("nl_be"),
        new KnownLocale("nl_nl"),
        new KnownLocale("nn_no"),
        new KnownLocale("no_no"),
        new KnownLocale("nb_no", "no"),
        new KnownLocale("oc_fr"),
        new KnownLocale("ovd", "sv"),
        new KnownLocale("pl_pl"),
        new KnownLocale("pls", "en"),
        new KnownLocale("pt_br"),
        new KnownLocale("pt_pt"),
        new KnownLocale("qcb_es"),
        new KnownLocale("qid", "id"),
        new KnownLocale("qya_aa"),
        new KnownLocale("ro_ro"),
        new KnownLocale("rpr", "ru"),
        new KnownLocale("ru_ru"),
        new KnownLocale("ry_ua"),
        new KnownLocale("sah_sah"),
        new KnownLocale("se_no"),
        new KnownLocale("sk_sk"),
        new KnownLocale("sl_si"),
        new KnownLocale("so_so"),
        new KnownLocale("sq_al"),
        new KnownLocale("sr_cs"),
        new KnownLocale("sr_sp"),
        new KnownLocale("sv_se"),
        new KnownLocale("sxu", "de"),
        new KnownLocale("szl", "pl"),
        new KnownLocale("ta_in"),
        new KnownLocale("th_th"),
        new KnownLocale("tl_ph"),
        new KnownLocale("tlh_aa"),
        new KnownLocale("tok"),
        new KnownLocale("tr_tr"),
        new KnownLocale("tt_ru"),
        new KnownLocale("tzo_mx"),
        new KnownLocale("uk_ua"),
        new KnownLocale("val_es"),
        new KnownLocale("vec_it"),
        new KnownLocale("vi_vn"),
        new KnownLocale("vp_vl"),
        new KnownLocale("yi_de"),
        new KnownLocale("yo_ng"),
        new KnownLocale("zh_cn"),
        new KnownLocale("zh_hk"),
        new KnownLocale("zh_tw"),
        new KnownLocale("zlm_arab")
    };
    private static Map<String, KnownLocale> knownLocalesByLowerCaseLocale = Arrays.stream(knownLocales).collect(Collectors.toMap(locale -> locale.lowerCaseLocale, Function.identity()));

    /**
     * @return The known Minecraft locale for the given string,
     * or null if no locale is known.
     */
    public static @Nullable KnownLocale getKnownLocale(String lowerCaseLocale) {
        return knownLocalesByLowerCaseLocale.get(lowerCaseLocale);
    }

    /**
     * @return The language group for a Minecraft locale,
     * or null if no language group is known.
     */
    public static @Nullable String getLanguageGroup(String lowerCaseLocale) {
        @Nullable KnownLocale knownLocale = getKnownLocale(lowerCaseLocale);
        return knownLocale != null ? knownLocale.languageGroup : computeLanguageGroup(lowerCaseLocale);
    }

    private static @Nullable String computeLanguageGroup(String lowerCaseLocale) {
        int underscoreIndex = lowerCaseLocale.indexOf('_');
        if (underscoreIndex > 0) {
            return lowerCaseLocale.substring(0, underscoreIndex);
        }
        return null;
    }

}
