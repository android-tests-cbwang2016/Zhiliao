package com.shatyuka.zhiliao;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Random;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class ZhihuPreference {
    final static String modulePackage = "com.shatyuka.zhiliao";

    private static Object preference_zhiliao;

    private static int version_click = 0;
    private static int author_click = 0;

    private static int settings_res_id = 0;
    private static int debug_res_id = 0;

    static boolean init(final ClassLoader classLoader) {
        try {
            XposedHelpers.findAndHookMethod(Helper.PreferenceInflater, "a", int.class, Helper.PreferenceGroup, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    XmlResourceParser parser;
                    int id = (int) param.args[0];
                    if (id == 7355608)
                        parser = Helper.modRes.getXml(R.xml.settings);
                    else if (id == debug_res_id)
                        parser = Helper.modRes.getXml(R.xml.preferences_zhihu);
                    else
                        return;
                    try {
                        param.setResult(Helper.inflate.invoke(param.thisObject, parser, param.args[1]));
                    } finally {
                        parser.close();
                    }
                }
            });

            XposedHelpers.findAndHookMethod(Helper.SettingsFragment, "i", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    settings_res_id = (int) param.getResult();
                }
            });
            XposedHelpers.findAndHookMethod(Helper.DebugFragment, "i", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    debug_res_id = (int) param.getResult();
                }
            });

            XposedBridge.hookMethod(Helper.addPreferencesFromResource, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (((int) param.args[0]) == settings_res_id) {
                        Helper.addPreferencesFromResource.invoke(param.thisObject, 7355608);
                    }
                }
            });

            XposedHelpers.findAndHookMethod(Helper.SettingsFragment, "onCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Object thisObject = param.thisObject;
                    preference_zhiliao = Helper.findPreference.invoke(thisObject, "preference_id_zhiliao");
                    Helper.setSummary.invoke(preference_zhiliao, "当前版本 " + Helper.modRes.getString(R.string.app_version));
                    Helper.setOnPreferenceClickListener.invoke(preference_zhiliao, thisObject);
                }
            });

            XposedHelpers.findAndHookMethod(Helper.SettingsFragment, "onPreferenceClick", Helper.Preference, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (param.args[0] == preference_zhiliao) {
                        Object thisObject = param.thisObject;
                        Method a = thisObject.getClass().getMethod("a", Helper.ZHIntent);
                        Object intent = Helper.ZHIntent.getConstructors()[0].newInstance(Helper.DebugFragment, null, "SCREEN_NAME_NULL", Array.newInstance(Helper.PageInfoType, 0));
                        a.invoke(thisObject, intent);
                        param.setResult(false);
                    }
                }
            });
            XposedBridge.hookMethod(Helper.DebugFragment.getMethod("a", Bundle.class, String.class), new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (param.thisObject.getClass() == Helper.DebugFragment) {
                        Field[] fields = Helper.PreferenceFragmentCompat.getDeclaredFields();
                        for (Field field : fields) {
                            if (field.getType() == Helper.PreferenceManager) {
                                field.setAccessible(true);
                                Helper.setSharedPreferencesName.invoke(field.get(param.thisObject), "zhiliao_preferences");
                                return;
                            }
                        }
                    }
                }
            });
            XposedHelpers.findAndHookMethod(Helper.BasePreferenceFragment, "onViewCreated", View.class, Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (param.thisObject.getClass() == Helper.DebugFragment) {
                        Field[] fields = Helper.BasePreferenceFragment.getDeclaredFields();
                        for (Field field : fields) {
                            if (field.getType().getName().equals("com.zhihu.android.app.ui.widget.SystemBar")) {
                                field.setAccessible(true);
                                Object systemBar = field.get(param.thisObject);
                                Object toolbar = systemBar.getClass().getMethod("getToolbar").invoke(systemBar);
                                toolbar.getClass().getMethod("setTitle", CharSequence.class).invoke(toolbar, "知了");
                                break;
                            }
                        }
                    }
                }
            });
            XposedHelpers.findAndHookMethod(Helper.DebugFragment, "h", new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    Object thisObject = param.thisObject;
                    Object preference_version = Helper.findPreference.invoke(thisObject, "preference_version");
                    Object preference_author = Helper.findPreference.invoke(thisObject, "preference_author");
                    Object preference_help = Helper.findPreference.invoke(thisObject, "preference_help");
                    Object preference_channel = Helper.findPreference.invoke(thisObject, "preference_channel");
                    Object preference_telegram = Helper.findPreference.invoke(thisObject, "preference_telegram");
                    Object preference_sourcecode = Helper.findPreference.invoke(thisObject, "preference_sourcecode");
                    Object preference_donate = Helper.findPreference.invoke(thisObject, "preference_donate");
                    Object preference_status = Helper.findPreference.invoke(thisObject, "preference_status");
                    Object switch_externlink = Helper.findPreference.invoke(thisObject, "switch_externlink");
                    Object switch_externlinkex = Helper.findPreference.invoke(thisObject, "switch_externlinkex");
                    Object switch_tag = Helper.findPreference.invoke(thisObject, "switch_tag");
                    Object switch_livebutton = Helper.findPreference.invoke(thisObject, "switch_livebutton");
                    Object switch_reddot = Helper.findPreference.invoke(thisObject, "switch_reddot");
                    Object switch_vipbanner = Helper.findPreference.invoke(thisObject, "switch_vipbanner");
                    Object switch_vipnav = Helper.findPreference.invoke(thisObject, "switch_vipnav");
                    Object switch_videonav = Helper.findPreference.invoke(thisObject, "switch_videonav");
                    Object switch_article = Helper.findPreference.invoke(thisObject, "switch_article");
                    Object switch_horizontal = Helper.findPreference.invoke(thisObject, "switch_horizontal");
                    Object switch_nextanswer = Helper.findPreference.invoke(thisObject, "switch_nextanswer");

                    Helper.setOnPreferenceChangeListener.invoke(Helper.findPreference.invoke(thisObject, "accept_eula"), thisObject);
                    Helper.setOnPreferenceClickListener.invoke(switch_externlink, thisObject);
                    Helper.setOnPreferenceClickListener.invoke(switch_externlinkex, thisObject);
                    Helper.setOnPreferenceClickListener.invoke(switch_tag, thisObject);
                    Helper.setOnPreferenceClickListener.invoke(switch_livebutton, thisObject);
                    Helper.setOnPreferenceClickListener.invoke(switch_reddot, thisObject);
                    Helper.setOnPreferenceClickListener.invoke(switch_vipbanner, thisObject);
                    Helper.setOnPreferenceClickListener.invoke(switch_vipnav, thisObject);
                    Helper.setOnPreferenceClickListener.invoke(switch_videonav, thisObject);
                    Helper.setOnPreferenceClickListener.invoke(switch_article, thisObject);
                    Helper.setOnPreferenceClickListener.invoke(switch_horizontal, thisObject);
                    Helper.setOnPreferenceClickListener.invoke(switch_nextanswer, thisObject);
                    Helper.setOnPreferenceClickListener.invoke(preference_version, thisObject);
                    Helper.setOnPreferenceClickListener.invoke(preference_author, thisObject);
                    Helper.setOnPreferenceClickListener.invoke(preference_help, thisObject);
                    Helper.setOnPreferenceClickListener.invoke(preference_channel, thisObject);
                    Helper.setOnPreferenceClickListener.invoke(preference_telegram, thisObject);
                    Helper.setOnPreferenceClickListener.invoke(preference_sourcecode, thisObject);
                    Helper.setOnPreferenceClickListener.invoke(preference_donate, thisObject);

                    String real_version = null;
                    try {
                        real_version = Helper.context.getPackageManager().getResourcesForApplication(modulePackage).getString(R.string.app_version);
                    } catch (Exception ignore) {
                    }
                    String loaded_version = Helper.modRes.getString(R.string.app_version);
                    Helper.setSummary.invoke(preference_version, loaded_version);
                    if (real_version == null || loaded_version.equals(real_version)) {
                        Helper.setVisible.invoke(preference_status, false);
                    } else {
                        Helper.setOnPreferenceClickListener.invoke(preference_status, thisObject);
                        Object category_eula = Helper.findPreference.invoke(thisObject, "category_eula");
                        Object category_ads = Helper.findPreference.invoke(thisObject, "category_ads");
                        Object category_misc = Helper.findPreference.invoke(thisObject, "category_misc");
                        Object category_ui = Helper.findPreference.invoke(thisObject, "category_ui");
                        Object category_swap_answers = Helper.findPreference.invoke(thisObject, "category_swap_answers");
                        Object category_filter = Helper.findPreference.invoke(thisObject, "category_filter");
                        Helper.setVisible.invoke(category_eula, false);
                        Helper.setVisible.invoke(category_ads, false);
                        Helper.setVisible.invoke(category_misc, false);
                        Helper.setVisible.invoke(category_ui, false);
                        Helper.setVisible.invoke(category_swap_answers, false);
                        Helper.setVisible.invoke(category_filter, false);
                        return null;
                    }

                    Helper.setIcon.invoke(preference_status, Helper.modRes.getDrawable(R.drawable.ic_refresh));
                    Helper.setIcon.invoke(Helper.findPreference.invoke(thisObject, "switch_mainswitch"), Helper.modRes.getDrawable(R.drawable.ic_toggle_on));
                    Helper.setIcon.invoke(Helper.findPreference.invoke(thisObject, "switch_launchad"), Helper.modRes.getDrawable(R.drawable.ic_ad_units));
                    Helper.setIcon.invoke(Helper.findPreference.invoke(thisObject, "switch_feedad"), Helper.modRes.getDrawable(R.drawable.ic_table_rows));
                    Helper.setIcon.invoke(Helper.findPreference.invoke(thisObject, "switch_answerlistad"), Helper.modRes.getDrawable(R.drawable.ic_format_list));
                    Helper.setIcon.invoke(Helper.findPreference.invoke(thisObject, "switch_commentad"), Helper.modRes.getDrawable(R.drawable.ic_comment));
                    Helper.setIcon.invoke(Helper.findPreference.invoke(thisObject, "switch_sharead"), Helper.modRes.getDrawable(R.drawable.ic_share));
                    Helper.setIcon.invoke(Helper.findPreference.invoke(thisObject, "switch_answerad"), Helper.modRes.getDrawable(R.drawable.ic_notes));
                    Helper.setIcon.invoke(Helper.findPreference.invoke(thisObject, "switch_searchad"), Helper.modRes.getDrawable(R.drawable.ic_search));
                    Helper.setIcon.invoke(Helper.findPreference.invoke(thisObject, "switch_video"), Helper.modRes.getDrawable(R.drawable.ic_play_circle));
                    Helper.setIcon.invoke(Helper.findPreference.invoke(thisObject, "switch_marketcard"), Helper.modRes.getDrawable(R.drawable.ic_vip));
                    Helper.setIcon.invoke(Helper.findPreference.invoke(thisObject, "switch_club"), Helper.modRes.getDrawable(R.drawable.ic_group));
                    Helper.setIcon.invoke(Helper.findPreference.invoke(thisObject, "switch_goods"), Helper.modRes.getDrawable(R.drawable.ic_local_mall));
                    Helper.setIcon.invoke(switch_externlink, Helper.modRes.getDrawable(R.drawable.ic_link));
                    Helper.setIcon.invoke(switch_externlinkex, Helper.modRes.getDrawable(R.drawable.ic_link));
                    Helper.setIcon.invoke(Helper.findPreference.invoke(thisObject, "switch_colormode"), Helper.modRes.getDrawable(R.drawable.ic_color));
                    Helper.setIcon.invoke(switch_tag, Helper.modRes.getDrawable(R.drawable.ic_label));
                    Helper.setIcon.invoke(switch_livebutton, Helper.modRes.getDrawable(R.drawable.ic_live_tv));
                    Helper.setIcon.invoke(switch_reddot, Helper.modRes.getDrawable(R.drawable.ic_mark_chat_unread));
                    Helper.setIcon.invoke(switch_vipbanner, Helper.modRes.getDrawable(R.drawable.ic_vip_banner));
                    Helper.setIcon.invoke(switch_vipnav, Helper.modRes.getDrawable(R.drawable.ic_vip_nav));
                    Helper.setIcon.invoke(switch_videonav, Helper.modRes.getDrawable(R.drawable.ic_play_circle));
                    Helper.setIcon.invoke(Helper.findPreference.invoke(thisObject, "switch_hotbanner"), Helper.modRes.getDrawable(R.drawable.ic_whatshot));
                    Helper.setIcon.invoke(switch_article, Helper.modRes.getDrawable(R.drawable.ic_article));
                    Helper.setIcon.invoke(switch_horizontal, Helper.modRes.getDrawable(R.drawable.ic_swap_horiz));
                    Helper.setIcon.invoke(switch_nextanswer, Helper.modRes.getDrawable(R.drawable.ic_circle_down));
                    Helper.setIcon.invoke(Helper.findPreference.invoke(thisObject, "edit_title"), Helper.regex_title != null ? Helper.modRes.getDrawable(R.drawable.ic_check) : Helper.modRes.getDrawable(R.drawable.ic_close));
                    Helper.setIcon.invoke(Helper.findPreference.invoke(thisObject, "edit_author"), Helper.regex_author != null ? Helper.modRes.getDrawable(R.drawable.ic_check) : Helper.modRes.getDrawable(R.drawable.ic_close));
                    Helper.setIcon.invoke(Helper.findPreference.invoke(thisObject, "edit_content"), Helper.regex_content != null ? Helper.modRes.getDrawable(R.drawable.ic_check) : Helper.modRes.getDrawable(R.drawable.ic_close));
                    Helper.setIcon.invoke(preference_version, Helper.modRes.getDrawable(R.drawable.ic_info));
                    Helper.setIcon.invoke(preference_author, Helper.modRes.getDrawable(R.drawable.ic_person));
                    Helper.setIcon.invoke(preference_help, Helper.modRes.getDrawable(R.drawable.ic_help));
                    Helper.setIcon.invoke(preference_channel, Helper.modRes.getDrawable(R.drawable.ic_rss_feed));
                    Helper.setIcon.invoke(preference_telegram, Helper.modRes.getDrawable(R.drawable.ic_telegram));
                    Helper.setIcon.invoke(preference_sourcecode, Helper.modRes.getDrawable(R.drawable.ic_github));
                    Helper.setIcon.invoke(preference_donate, Helper.modRes.getDrawable(R.drawable.ic_monetization));

                    if (Helper.prefs.getBoolean("accept_eula", false)) {
                        Object category_eula = Helper.findPreference.invoke(thisObject, "category_eula");
                        Helper.setVisible.invoke(category_eula, false);
                    } else {
                        Object switch_main = Helper.findPreference.invoke(param.thisObject, "switch_mainswitch");
                        Helper.setChecked.invoke(switch_main, false);
                    }
                    return null;
                }
            });
            XposedHelpers.findAndHookMethod(Helper.DebugFragment, "onPreferenceClick", "androidx.preference.Preference", new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    Object preference = param.args[0];
                    switch ((String) Helper.getKey.invoke(preference)) {
                        case "preference_status":
                            System.exit(0);
                            break;
                        case "preference_version":
                            version_click++;
                            if (version_click == 5) {
                                Toast.makeText(Helper.context, "点我次数再多，更新也不会变快哦", Toast.LENGTH_SHORT).show();
                                version_click = 0;
                            }
                            break;
                        case "preference_author":
                            author_click++;
                            if (author_click == 5) {
                                Toast.makeText(Helper.context, Helper.modRes.getStringArray(R.array.click_author)[new Random().nextInt(4)], Toast.LENGTH_SHORT).show();
                                author_click = 0;
                            }
                            break;
                        case "preference_help":
                            Uri uri_help = Uri.parse("https://github.com/shatyuka/Zhiliao/wiki");
                            Intent intent_help = new Intent(Intent.ACTION_VIEW, uri_help);
                            ((Context) Helper.getContext.invoke(param.thisObject)).startActivity(intent_help);
                            break;
                        case "preference_channel":
                            Uri uri_channel = Uri.parse("https://t.me/zhiliao");
                            Intent intent_channel = new Intent(Intent.ACTION_VIEW, uri_channel);
                            ((Context) Helper.getContext.invoke(param.thisObject)).startActivity(intent_channel);
                            break;
                        case "preference_telegram":
                            Uri uri_telegram = Uri.parse("https://t.me/joinchat/OibCWxbdCMkJ2fG8J1DpQQ");
                            Intent intent_telegram = new Intent(Intent.ACTION_VIEW, uri_telegram);
                            ((Context) Helper.getContext.invoke(param.thisObject)).startActivity(intent_telegram);
                            break;
                        case "preference_sourcecode":
                            Uri uri_sourcecode = Uri.parse("https://github.com/shatyuka/Zhiliao");
                            Intent intent_sourcecode = new Intent(Intent.ACTION_VIEW, uri_sourcecode);
                            ((Context) Helper.getContext.invoke(param.thisObject)).startActivity(intent_sourcecode);
                            break;
                        case "preference_donate":
                            Intent donate_intent = new Intent();
                            donate_intent.setAction(Intent.ACTION_MAIN);
                            donate_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            donate_intent.putExtra("zhiliao_donate", true);
                            donate_intent.setClassName(modulePackage, "com.shatyuka.zhiliao.SettingsActivity");
                            ((Context) Helper.getContext.invoke(param.thisObject)).startActivity(donate_intent);
                            break;
                        case "switch_externlink":
                            Object switch_externlinkex = Helper.findPreference.invoke(param.thisObject, "switch_externlinkex");
                            Helper.setChecked.invoke(switch_externlinkex, false);
                            break;
                        case "switch_externlinkex":
                            Object switch_externlink = Helper.findPreference.invoke(param.thisObject, "switch_externlink");
                            Helper.setChecked.invoke(switch_externlink, false);
                            break;
                        case "switch_tag":
                        case "switch_livebutton":
                        case "switch_reddot":
                        case "switch_vipbanner":
                        case "switch_vipnav":
                        case "switch_videonav":
                        case "switch_article":
                        case "switch_horizontal":
                        case "switch_nextanswer":
                            Toast.makeText(Helper.context, "重启知乎生效", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    return false;
                }
            });
            XposedHelpers.findAndHookMethod(Helper.DebugFragment, "a", "androidx.preference.Preference", Object.class, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    if ((boolean) param.args[1]) {
                        Object switch_main = Helper.findPreference.invoke(param.thisObject, "switch_mainswitch");
                        Helper.setChecked.invoke(switch_main, true);
                        Object category_eula = Helper.findPreference.invoke(param.thisObject, "category_eula");
                        Helper.setVisible.invoke(category_eula, false);
                    }
                    return true;
                }
            });
            XposedHelpers.findAndHookMethod(Helper.EditTextPreference, "a", String.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Object thisObject = param.thisObject;
                    switch ((String) Helper.getKey.invoke(thisObject)) {
                        case "edit_title":
                            Helper.regex_title = Helper.compileRegex((String) Helper.getText.invoke(thisObject));
                            Helper.setIcon.invoke(thisObject, Helper.regex_title != null ? Helper.modRes.getDrawable(R.drawable.ic_check) : Helper.modRes.getDrawable(R.drawable.ic_close));
                            break;
                        case "edit_author":
                            Helper.regex_author = Helper.compileRegex((String) Helper.getText.invoke(thisObject));
                            Helper.setIcon.invoke(thisObject, Helper.regex_author != null ? Helper.modRes.getDrawable(R.drawable.ic_check) : Helper.modRes.getDrawable(R.drawable.ic_close));
                            break;
                        case "edit_content":
                            Helper.regex_content = Helper.compileRegex((String) Helper.getText.invoke(thisObject));
                            Helper.setIcon.invoke(thisObject, Helper.regex_content != null ? Helper.modRes.getDrawable(R.drawable.ic_check) : Helper.modRes.getDrawable(R.drawable.ic_close));
                            break;
                    }
                }
            });

            XposedHelpers.findAndHookMethod(Helper.MainActivity, "onCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Object thisObject = param.thisObject;
                    Intent intent = ((Activity) thisObject).getIntent();
                    if (intent.hasExtra("zhiliao_settings")) {
                        if (Helper.addFragmentToOverlay != null)
                            Helper.addFragmentToOverlay.invoke(thisObject, Helper.ZHIntent.getConstructors()[0].newInstance(Helper.DebugFragment, null, "SCREEN_NAME_NULL", Array.newInstance(Helper.PageInfoType, 0)));
                        else
                            Helper.addFragmentToOverlay_old.invoke(thisObject, Helper.ZHIntent.getConstructors()[0].newInstance(Helper.DebugFragment, null, "SCREEN_NAME_NULL", Array.newInstance(Helper.PageInfoType, 0)), null, 0, null, false);
                    }
                }
            });
            XposedHelpers.findAndHookMethod(Helper.MainActivity, "onNewIntent", Intent.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Object thisObject = param.thisObject;
                    Intent intent = (Intent) param.args[0];
                    if (intent.hasExtra("zhiliao_settings")) {
                        if (Helper.addFragmentToOverlay != null)
                            Helper.addFragmentToOverlay.invoke(thisObject, Helper.ZHIntent.getConstructors()[0].newInstance(Helper.DebugFragment, null, "SCREEN_NAME_NULL", Array.newInstance(Helper.PageInfoType, 0)));
                        else
                            Helper.addFragmentToOverlay_old.invoke(thisObject, Helper.ZHIntent.getConstructors()[0].newInstance(Helper.DebugFragment, null, "SCREEN_NAME_NULL", Array.newInstance(Helper.PageInfoType, 0)), null, 0, null, false);                    }
                }
            });

            XposedHelpers.findAndHookMethod(Dialog.class, "dismiss", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    Dialog dialog = (Dialog) param.thisObject;
                    if (dialog.isShowing()) {
                        View view = dialog.getWindow().getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager) Helper.context.getSystemService(Activity.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getRootView().getWindowToken(), 0);
                        }
                    }
                }
            });
            return true;
        } catch (NoSuchMethodException e) {
            XposedBridge.log(e.getMessage());
            return false;
        }
    }
}
