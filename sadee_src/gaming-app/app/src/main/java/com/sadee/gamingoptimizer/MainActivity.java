package com.sadee.gamingoptimizer;

import android.app.*;
import android.os.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.view.animation.AlphaAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Animation;
import android.provider.Settings;
import android.os.StatFs;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.*;
import android.widget.*;
import java.net.*;
import java.io.*;
import org.json.*;

public class MainActivity extends Activity {
    final int BG=Color.rgb(3,5,10), PANEL=Color.rgb(9,14,23), CYAN=Color.rgb(0,255,213),
            RED=Color.rgb(255,23,68), PURPLE=Color.rgb(168,85,247), WHITE=Color.WHITE,
            MUTED=Color.rgb(141,152,168), GREEN=Color.rgb(25,190,105);
    LinearLayout root, content;
    String userName="", licenseKey="", selectedGame="Free Fire";
    boolean activated=false;
    SharedPreferences prefs;
    String SUPABASE_URL="https://cpmzjplfqggmgeotmtcd.supabase.co";
    String SUPABASE_ANON_KEY="sb_publishable_z0kmWe85ybkmBg7bzEtyKA_kMH6WGXW";

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);
        prefs=getSharedPreferences("sadee_session",MODE_PRIVATE);
        showSplash();
    }

    int dp(int n){return (int)(n*getResources().getDisplayMetrics().density+0.5f);}
    GradientDrawable box(int c,int s){
        GradientDrawable g=new GradientDrawable(); g.setColor(c); g.setCornerRadius(dp(18));
        if(s!=0)g.setStroke(dp(2),s); return g;
    }
    TextView tv(String s,int z,int c){
        TextView v=new TextView(this); v.setText(s); v.setTextSize(z); v.setTextColor(c);
        v.setPadding(dp(14),dp(10),dp(14),dp(10)); return v;
    }
    Button btn(String s,int c,int st){
        Button b=new Button(this); b.setText(s); b.setTextColor(WHITE); b.setTextSize(13);
        b.setAllCaps(false); b.setGravity(Gravity.CENTER); b.setBackground(box(c,st));
        b.setPadding(dp(6),0,dp(6),0); return b;
    }
    void base(){
        root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setBackgroundColor(BG);
        ScrollView sc=new ScrollView(this); sc.setFillViewport(true);
        content=new LinearLayout(this); content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(16),dp(10),dp(16),dp(18)); sc.addView(content);
        root.addView(sc,new LinearLayout.LayoutParams(-1,0,1)); setContentView(root);
    }
    void banner(String file){
        int id=getResources().getIdentifier(file,"drawable",getPackageName());
        if(id!=0){
            ImageView im=new ImageView(this); im.setImageResource(id); im.setScaleType(ImageView.ScaleType.CENTER_CROP);
            im.setBackground(box(PANEL,CYAN));
            LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,dp(175)); p.setMargins(0,dp(4),0,dp(12));
            content.addView(im,p);
        }
    }
    void brand(String page,String asset){
        TextView top=tv("𝕤𝕒𝕕𝕖𝕖 𝕏 𝕕𝕚𝕞𝕒",25,CYAN); top.setGravity(Gravity.CENTER);
        top.setTypeface(Typeface.DEFAULT,Typeface.BOLD); content.addView(top);
        TextView sub=tv("GAMING PERFORMANCE  •  "+page,10,MUTED); sub.setGravity(Gravity.CENTER); content.addView(sub);
        banner(asset);
    }
    TextView card(String s,int accent){
        TextView v=tv(s,14,WHITE); v.setBackground(box(PANEL,accent));
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2); p.setMargins(0,dp(6),0,dp(6));
        content.addView(v,p); return v;
    }
    TextView label(String s){
        TextView v=tv(s,12,MUTED); v.setTypeface(Typeface.DEFAULT,Typeface.BOLD); content.addView(v); return v;
    }
    EditText field(String h){
        EditText e=new EditText(this); e.setHint(h); e.setHintTextColor(MUTED); e.setTextColor(WHITE);
        e.setSingleLine(); e.setTextSize(16); e.setPadding(dp(15),dp(8),dp(15),dp(8));
        e.setBackground(box(Color.TRANSPARENT,CYAN));
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,dp(58)); p.setMargins(0,dp(5),0,dp(5));
        content.addView(e,p); return e;
    }
    void nav(){
        LinearLayout n=new LinearLayout(this); n.setBackgroundColor(Color.rgb(6,9,15)); n.setPadding(0,dp(2),0,dp(2));
        String[] a={"⌂\nHOME","🎮\nGAMES","⚡\nBOOST","📊\nMONITOR","👤\nPROFILE"};
        for(int i=0;i<a.length;i++){
            Button b=btn(a[i],Color.TRANSPARENT,i==0?CYAN:PURPLE); final int x=i;
            b.setTextSize(11); b.setOnClickListener(v->{if(x==0)showHome();else if(x==1)showGames();else if(x==2)showBoost();else if(x==3)showMonitor();else showProfile();});
            n.addView(b,new LinearLayout.LayoutParams(0,dp(68),1));
        }
        root.addView(n);
    }
    TextView statCard(String title,String value,int accent){
        LinearLayout r=new LinearLayout(this); r.setOrientation(LinearLayout.VERTICAL); r.setPadding(dp(12),dp(8),dp(12),dp(8));
        r.setBackground(box(PANEL,accent));
        TextView t=tv(title,11,MUTED); t.setGravity(Gravity.CENTER); r.addView(t);
        TextView v=tv(value,20,WHITE); v.setGravity(Gravity.CENTER); v.setTypeface(Typeface.DEFAULT,Typeface.BOLD); r.addView(v);
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,dp(88),1); p.setMargins(dp(3),dp(3),dp(3),dp(3));
        content.addView(r,p); return v;
    }
    void metricRow(String a,String b,String c,String d,int accent){
        LinearLayout row=new LinearLayout(this); row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0,dp(2),0,dp(2)); content.addView(row);
        statCardInto(row,a,b,accent); statCardInto(row,c,d,PURPLE);
    }
    void statCardInto(LinearLayout row,String title,String value,int accent){
        LinearLayout r=new LinearLayout(this); r.setOrientation(LinearLayout.VERTICAL); r.setGravity(Gravity.CENTER);
        r.setPadding(dp(6),dp(5),dp(6),dp(5)); r.setBackground(box(PANEL,accent));
        TextView t=tv(title,10,MUTED); t.setGravity(Gravity.CENTER); r.addView(t);
        TextView v=tv(value,18,WHITE); v.setGravity(Gravity.CENTER); v.setTypeface(Typeface.DEFAULT,Typeface.BOLD); r.addView(v);
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,dp(80),1); p.setMargins(dp(3),0,dp(3),0); row.addView(r,p);
    }

    void showSplash(){
        root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setGravity(Gravity.CENTER);
        root.setPadding(dp(24),dp(24),dp(24),dp(24)); root.setBackgroundColor(BG);
        ImageView im=new ImageView(this); im.setImageResource(R.drawable.sadee_logo); im.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        root.addView(im,new LinearLayout.LayoutParams(-1,dp(260)));
        TextView name=tv("𝕤𝕒𝕕𝕖𝕖 𝕏 𝕕𝕚𝕞𝕒",30,CYAN); name.setGravity(17); name.setTypeface(Typeface.DEFAULT,Typeface.BOLD); root.addView(name);
        TextView sub=tv("GAMING PERFORMANCE OPTIMIZER",12,MUTED); sub.setGravity(17); root.addView(sub);
        ProgressBar pb=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal); pb.setMax(100); pb.setProgress(0);
        LinearLayout.LayoutParams pp=new LinearLayout.LayoutParams(-1,dp(8)); pp.setMargins(dp(45),dp(24),dp(45),dp(8)); root.addView(pb,pp);
        TextView loading=tv("LOADING  •  INITIALIZING...",12,WHITE); loading.setGravity(17); root.addView(loading); setContentView(root);
        Animation pulse=new ScaleAnimation(0.82f,1.05f,0.82f,1.05f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        pulse.setDuration(850); pulse.setRepeatMode(Animation.REVERSE); pulse.setRepeatCount(Animation.INFINITE); im.startAnimation(pulse);
        AlphaAnimation glow=new AlphaAnimation(0.45f,1f); glow.setDuration(700); glow.setRepeatMode(Animation.REVERSE); glow.setRepeatCount(Animation.INFINITE); name.startAnimation(glow);
        new Thread(()->{for(int i=0;i<=100;i+=5){final int n=i;runOnUiThread(()->{pb.setProgress(n);loading.setText(n<100?"LOADING  •  "+n+"%":"READY  •  SADEE X DIMA");});try{Thread.sleep(35);}catch(Exception ignored){}}runOnUiThread(this::continueAfterSplash);}).start();
    }

    void continueAfterSplash(){
        String savedUser=prefs.getString("username","");
        String savedKey=prefs.getString("license_key","");
        boolean remembered=prefs.getBoolean("remembered",false);
        if(remembered && !savedUser.isEmpty() && !savedKey.isEmpty()){
            userName=savedUser; licenseKey=savedKey;
            if(SUPABASE_URL.isEmpty()||SUPABASE_ANON_KEY.isEmpty()){
                activated=true; showHome();
            }else{
                // Revalidate the saved session without asking for credentials again.
                verifyLicense(savedUser,savedKey,null);
            }
        }else showLogin();
    }

    void showLogin(){
        base(); banner("home_banner");
        TextView d=tv("LICENSE ACTIVATION",24,WHITE); d.setGravity(17); d.setTypeface(Typeface.DEFAULT,Typeface.BOLD); content.addView(d);
        TextView sub=tv("UNLOCK YOUR GAMING PERFORMANCE",11,MUTED); sub.setGravity(17); content.addView(sub);
        EditText u=field("USERNAME"), k=field("LICENSE KEY");
        Button a=btn("⚡  ACTIVATE LICENSE",CYAN,CYAN); a.setTextColor(Color.BLACK); content.addView(a,new LinearLayout.LayoutParams(-1,dp(60)));
        TextView st=tv("●  LICENSE SYSTEM READY",13,CYAN); st.setGravity(17); content.addView(st);
        TextView buy=tv("💎  PREMIUM PLANS",21,WHITE); buy.setTypeface(Typeface.DEFAULT,Typeface.BOLD); content.addView(buy);
        String[][] plans={{"7 DAYS","Rs. 500"},{"1 MONTH","Rs. 1,000"},{"LIFETIME","Rs. 2,200"}};
        for(String[] p:plans){
            LinearLayout r=new LinearLayout(this); r.setGravity(Gravity.CENTER_VERTICAL); r.setPadding(dp(12),dp(7),dp(12),dp(7)); r.setBackground(box(PANEL,PURPLE));
            TextView x=tv("◆ "+p[0]+"\n   "+p[1],15,WHITE); x.setTypeface(Typeface.DEFAULT,Typeface.BOLD); r.addView(x,new LinearLayout.LayoutParams(0,dp(70),1));
            Button q=btn("BUY / SELECT",PURPLE,PURPLE); r.addView(q,new LinearLayout.LayoutParams(dp(145),dp(54))); content.addView(r);
            q.setOnClickListener(v->orderWhatsApp(p[0],p[1]));
        }
        Button wa=btn("🟢  JOIN WHATSAPP GROUP",GREEN,GREEN); content.addView(wa); wa.setOnClickListener(v->open("https://chat.whatsapp.com/I3BsxvZMWM752XxJygaljp?s=cl&p=a&mlu=4&ilr=4"));
        Button tt=btn("♪  FOLLOW ON TIKTOK",Color.rgb(20,23,31),RED); content.addView(tt); tt.setOnClickListener(v->open("https://www.tiktok.com/@sadeexdima.optimizer?_r=1&_t=ZS-9AI4MbIgK4T"));
        a.setOnClickListener(v->{String us=u.getText().toString().trim().replaceAll("\u200B|\uFEFF", ""),key=k.getText().toString().trim().replaceAll("\u200B|\uFEFF", "");if(us.isEmpty()||key.isEmpty()){st.setText("●  ENTER USERNAME + LICENSE KEY");st.setTextColor(RED);return;}userName=us;licenseKey=key;if(SUPABASE_URL.isEmpty()||SUPABASE_ANON_KEY.isEmpty()){activated=true;st.setText("●  LOCAL SETUP MODE");new Handler().postDelayed(this::showHome,450);}else verifyLicense(us,key,st);});
    }
    void verifyLicense(String u,String key,TextView st){
        new Thread(()->{
            try{
                String did=android.provider.Settings.Secure.getString(
                        getContentResolver(),
                        android.provider.Settings.Secure.ANDROID_ID
                );

                URL url=new URL(SUPABASE_URL+"/rest/v1/rpc/validate_license");
                HttpURLConnection c=(HttpURLConnection)url.openConnection();
                c.setRequestMethod("POST");
                c.setDoOutput(true);
                c.setRequestProperty("Content-Type","application/json");
                c.setRequestProperty("apikey",SUPABASE_ANON_KEY);
                c.setRequestProperty("Authorization","Bearer "+SUPABASE_ANON_KEY);

                JSONObject req=new JSONObject();
                req.put("p_username",u);
                req.put("p_key_code",key);
                req.put("p_device_id",did);

                byte[] bytes=req.toString().getBytes("UTF-8");
                OutputStream os=c.getOutputStream();
                os.write(bytes);
                os.flush();
                os.close();

                int code=c.getResponseCode();
                InputStream raw=(code>=200&&code<300)?c.getInputStream():c.getErrorStream();
                String body=new BufferedReader(new InputStreamReader(raw))
                        .lines().reduce("",(x,y)->x+y);

                if(code<200||code>=300){
                    throw new IOException("HTTP "+code+" "+body);
                }

                JSONObject result=new JSONObject(body);
                boolean valid=result.optBoolean("valid",false);
                String reason=result.optString("reason","");

                runOnUiThread(()->{
                    if(valid){
                        activated=true;
                        prefs.edit().putString("username",u).putString("license_key",key).putBoolean("remembered",true).apply();
                        if(st!=null){ st.setText("● LICENSE ACTIVE"); }
                        if(st!=null) st.setTextColor(CYAN);
                        updateDeviceMetadata(result.optString("id",""));
                        showHome();
                    }else{
                        if(st!=null && "expired".equals(reason)){
                            st.setText("● LICENSE EXPIRED");
                        }else if(st!=null && ("disabled".equals(reason)||"revoked".equals(reason))){
                            st.setText("● LICENSE DISABLED / REVOKED");
                        }else if(st!=null && "device_limit".equals(reason)){
                            st.setText("● DEVICE LIMIT REACHED");
                        }else if(st!=null){
                            st.setText("● INVALID LICENSE" + (reason.isEmpty() ? "" : " (" + reason + ")"));
                        }
                        if(st!=null) st.setTextColor(RED);
                        else { prefs.edit().clear().apply(); activated=false; showLogin(); }
                    }
                });
            }catch(Exception e){
                runOnUiThread(()->{
                    if(st!=null){ st.setText("● LICENSE SERVER ERROR"); st.setTextColor(RED); }
                    else showLogin();
                });
            }
        }).start();
    }
    void updateDeviceMetadata(String licenseId){
        if(licenseId==null||licenseId.isEmpty())return;
        new Thread(()->{try{
            String did=android.provider.Settings.Secure.getString(getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);
            JSONObject o=new JSONObject();
            o.put("p_license_id",licenseId);
            o.put("p_device_id",did);
            o.put("p_device_name",shortDeviceName());
            o.put("p_android_version",Build.VERSION.RELEASE);
            o.put("p_ram",ramSummary());
            o.put("p_storage",storageSummary());
            o.put("p_cpu",Build.HARDWARE+" / "+Runtime.getRuntime().availableProcessors()+" cores");
            URL u=new URL(SUPABASE_URL+"/rest/v1/rpc/update_device_metadata");
            HttpURLConnection c=(HttpURLConnection)u.openConnection(); c.setRequestMethod("POST"); c.setDoOutput(true);
            c.setRequestProperty("apikey",SUPABASE_ANON_KEY); c.setRequestProperty("Authorization","Bearer "+SUPABASE_ANON_KEY); c.setRequestProperty("Content-Type","application/json");
            byte[] bytes=o.toString().getBytes("UTF-8"); c.getOutputStream().write(bytes); c.getOutputStream().close(); c.getResponseCode(); c.disconnect();
        }catch(Exception ignored){}}).start();
    }

    void orderWhatsApp(String plan,String price){open("https://wa.me/94768472404?text="+Uri.encode("Hello SADEE X DIMA, I want to order the "+plan+" License - "+price+". Username: "+(userName.isEmpty()?"Not set":userName)));}
    void registerDevice(String licenseId){
        if(licenseId==null||licenseId.isEmpty())return;
        new Thread(()->{try{String did=android.provider.Settings.Secure.getString(getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);String name=Build.MANUFACTURER+" "+Build.MODEL;
            JSONObject o=new JSONObject();o.put("license_key_id",licenseId);o.put("device_id",did);o.put("device_name",name);o.put("android_version",Build.VERSION.RELEASE);o.put("ram",String.valueOf(ram())+"% used");o.put("storage",String.valueOf(storage())+"% used");o.put("cpu",Build.HARDWARE);
            byte[] bytes=o.toString().getBytes("UTF-8");URL u=new URL(SUPABASE_URL+"/rest/v1/license_devices?on_conflict=license_key_id,device_id");HttpURLConnection c=(HttpURLConnection)u.openConnection();c.setRequestMethod("POST");c.setDoOutput(true);
            c.setRequestProperty("apikey",SUPABASE_ANON_KEY);c.setRequestProperty("Authorization","Bearer "+SUPABASE_ANON_KEY);c.setRequestProperty("Content-Type","application/json");c.setRequestProperty("Prefer","resolution=merge-duplicates,return=minimal");c.getOutputStream().write(bytes);c.getInputStream().close();
        }catch(Exception ignored){}}).start();
    }

    void showHome(){
        if(!activated){showLogin();return;} base(); brand("HOME","home_banner");
        TextView welcome=card("👤  "+userName+"   •   🟢 LICENSE ACTIVE\n🎮  "+selectedGame+"  •  READY TO BOOST",CYAN); welcome.setTextSize(14);
        metricRow("RAM",ram()+"%","BATTERY",battery()+"%",CYAN);
        metricRow("STORAGE",storage()+"%","TEMP",temperature(),RED);
        TextView h=tv("QUICK ACTIONS",15,WHITE); h.setTypeface(Typeface.DEFAULT,Typeface.BOLD); content.addView(h);
        Button games=btn("🎮  CHOOSE GAME",PURPLE,PURPLE); content.addView(games); games.setOnClickListener(v->showGames());
        Button boost=btn("⚡  BOOST PERFORMANCE",CYAN,CYAN); boost.setTextColor(Color.BLACK); content.addView(boost); boost.setOnClickListener(v->showBoost());
        Button mon=btn("📊  OPEN LIVE MONITOR",PANEL,RED); content.addView(mon); mon.setOnClickListener(v->showMonitor());
        TextView dev=tv("📱 DEVICE & PERFORMANCE",15,WHITE); dev.setTypeface(Typeface.DEFAULT,Typeface.BOLD); content.addView(dev);
        metricRow("DEVICE",shortDeviceName(),"ANDROID",Build.VERSION.RELEASE,CYAN);
        metricRow("RAM",ramSummary(),"STORAGE",storageSummary(),PURPLE);
        metricRow("CPU CORES",String.valueOf(Runtime.getRuntime().availableProcessors()),"CPU",Build.HARDWARE,RED);
        Button clear=btn("🧹  CLEAR CACHE / TEMP",PANEL,CYAN); content.addView(clear); clear.setOnClickListener(v->clearOwnCache());
        Button storageBtn=btn("💾  STORAGE CLEANUP CENTER",PANEL,PURPLE); content.addView(storageBtn); storageBtn.setOnClickListener(v->openStorageSettings());
        Button refresh=btn("↻  REFRESH DEVICE STATUS",PANEL,RED); content.addView(refresh); refresh.setOnClickListener(v->showHome());
        nav();
    }

    int ram(){try{ActivityManager am=(ActivityManager)getSystemService(ACTIVITY_SERVICE);ActivityManager.MemoryInfo mi=new ActivityManager.MemoryInfo();am.getMemoryInfo(mi);if(mi.totalMem<=0)return 0;long used=mi.totalMem-mi.availMem;return (int)Math.max(0,Math.min(100,(used*100L)/mi.totalMem));}catch(Exception e){return 0;}}
    String ramSummary(){try{ActivityManager am=(ActivityManager)getSystemService(ACTIVITY_SERVICE);ActivityManager.MemoryInfo mi=new ActivityManager.MemoryInfo();am.getMemoryInfo(mi);return formatBytes(mi.availMem)+" free / "+formatBytes(mi.totalMem);}catch(Exception e){return ram()+"%";}}
    int storage(){try{StatFs s=new StatFs(android.os.Environment.getDataDirectory().getAbsolutePath());long total=s.getTotalBytes(),free=s.getAvailableBytes();if(total<=0)return 0;return (int)Math.max(0,Math.min(100,((total-free)*100L)/total));}catch(Exception e){return 0;}}
    String storageSummary(){try{StatFs s=new StatFs(android.os.Environment.getDataDirectory().getAbsolutePath());return formatBytes(s.getAvailableBytes())+" free / "+formatBytes(s.getTotalBytes());}catch(Exception e){return storage()+"%";}}
    String formatBytes(long n){if(n<1024L*1024L)return n+" B";double v=n;String[] u={"KB","MB","GB","TB"};int i=-1;while(v>=1024&&i<u.length-1){v/=1024;i++;}return String.format(java.util.Locale.US,"%.1f %s",v,u[Math.max(0,i)]);}
    String shortDeviceName(){return Build.MANUFACTURER+" "+Build.MODEL;}
    void clearOwnCache(){try{deleteRecursive(getCacheDir());Toast.makeText(this,"✓ App temporary cache cleared",Toast.LENGTH_SHORT).show();}catch(Exception e){Toast.makeText(this,"Cleanup unavailable",Toast.LENGTH_SHORT).show();}}
    void deleteRecursive(File f){if(f==null||!f.exists())return;if(f.isDirectory()){File[] fs=f.listFiles();if(fs!=null)for(File x:fs)deleteRecursive(x);}if(!f.equals(getCacheDir()))f.delete();}
    void openStorageSettings(){try{Intent i=new Intent("android.settings.INTERNAL_STORAGE_SETTINGS");startActivity(i);}catch(Exception e){try{startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));}catch(Exception ignored){}}}
    int battery(){try{return ((android.os.BatteryManager)getSystemService(BATTERY_SERVICE)).getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_CAPACITY);}catch(Exception e){return 0;}}
    String temperature(){try{Intent i=registerReceiver(null,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));if(i!=null){int t=i.getIntExtra(android.os.BatteryManager.EXTRA_TEMPERATURE,Integer.MIN_VALUE);if(t!=Integer.MIN_VALUE)return String.format(java.util.Locale.US,"%.1f°C",t/10f);}}catch(Exception ignored){}return "N/A";}
    void optimizeMemory(){try{MainActivity.this.onTrimMemory(ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL);}catch(Exception ignored){}try{System.gc();System.runFinalization();System.gc();}catch(Exception ignored){}}
    void showOptimizeToast(){optimizeMemory();Toast.makeText(this,"✓ Memory optimized for SADEE X DIMA",Toast.LENGTH_SHORT).show();}

    void showGames(){
        base(); brand("GAMES","games_banner");
        TextView intro=card("SELECT YOUR GAME PROFILE\nEach profile prepares the optimizer for your selected game.",CYAN); intro.setTextSize(13);
        gameCard("FREE FIRE","freefire_art",CYAN,()->{selectedGame="Free Fire";showBoost();});
        gameCard("FREE FIRE MAX","freefire_art",RED,()->{selectedGame="Free Fire MAX";showBoost();});
        gameCard("PUBG MOBILE","pubg_art",PURPLE,()->{selectedGame="PUBG MOBILE";showBoost();});
        gameCard("COD MOBILE","cod_art",CYAN,()->{selectedGame="COD MOBILE";showBoost();});
        Button custom=btn("➕  CUSTOM GAME / PROFILE",PANEL,PURPLE); content.addView(custom); custom.setOnClickListener(v->{selectedGame="Custom Game";showBoost();});
        nav();
    }
    void gameCard(String title,String asset,int accent,View.OnClickListener click){
        LinearLayout c=new LinearLayout(this); c.setOrientation(LinearLayout.VERTICAL); c.setBackground(box(PANEL,accent)); c.setPadding(dp(5),dp(5),dp(5),dp(8));
        int id=getResources().getIdentifier(asset,"drawable",getPackageName());
        if(id!=0){ImageView im=new ImageView(this);im.setImageResource(id);im.setScaleType(ImageView.ScaleType.CENTER_CROP);c.addView(im,new LinearLayout.LayoutParams(-1,dp(145)));}
        LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(dp(8),0,dp(5),0);
        TextView t=tv("🎮  "+title,15,WHITE);t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);row.addView(t,new LinearLayout.LayoutParams(0,dp(55),1));
        Button b=btn("SELECT",accent,accent);row.addView(b,new LinearLayout.LayoutParams(dp(105),dp(48)));c.addView(row);
        b.setOnClickListener(click); c.setOnClickListener(click);
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.setMargins(0,dp(7),0,dp(7));content.addView(c,p);
    }

    void showBoost(){
        base(); brand("BOOST","boost_banner");
        card("🎮 SELECTED GAME\n"+selectedGame+"\n\n⚡ BOOST ENGINE READY",CYAN);
        TextView mode=card("PERFORMANCE MODE\nHIGH PERFORMANCE",RED);
        TextView h=tv("SELECT PERFORMANCE PROFILE",15,WHITE);h.setTypeface(Typeface.DEFAULT,Typeface.BOLD);content.addView(h);
        for(String x:new String[]{"BALANCED","HIGH PERFORMANCE","EXTREME GAMING"}){
            Button b=btn(x,PANEL,PURPLE);content.addView(b);b.setOnClickListener(v->mode.setText("PERFORMANCE MODE\n"+x));
        }
        metricRow("RAM",ram()+"%","BATTERY",battery()+"%",CYAN);
        Button mem=btn("🧹  OPTIMIZE MEMORY",CYAN,CYAN);mem.setTextColor(Color.BLACK);content.addView(mem);mem.setOnClickListener(v->showOptimizeToast());
        Button go=btn("🚀  BOOST & LAUNCH",RED,RED);content.addView(go);go.setOnClickListener(v->{optimizeMemory();go.setText("✓ BOOST READY");new Handler().postDelayed(this::showLaunch,650);});
        nav();
    }

    void showMonitor(){
        base(); brand("MONITOR","monitor_banner");
        metricRow("RAM USED",ram()+"%","STORAGE",storage()+"%",CYAN);
        metricRow("BATTERY",battery()+"%","TEMP",temperature(),RED);
        card("📱 DEVICE\n"+shortDeviceName()+"\nAndroid "+Build.VERSION.RELEASE+"\nCPU CORES  "+Runtime.getRuntime().availableProcessors()+"\nRAM  "+ramSummary()+"\nSTORAGE  "+storageSummary()+"\nCPU ABI  "+Build.SUPPORTED_ABIS[0],PURPLE);
        TextView live=card("🟢 LIVE STATUS\nGaming monitor is active.\nTap refresh to update the current readings.",CYAN);
        Button refresh=btn("↻  REFRESH LIVE DATA",PANEL,CYAN);content.addView(refresh);refresh.setOnClickListener(v->showMonitor());
        Button opt=btn("🧹  OPTIMIZE MEMORY",CYAN,CYAN);opt.setTextColor(Color.BLACK);content.addView(opt);opt.setOnClickListener(v->showOptimizeToast());
        nav();
    }

    String[] packagesForGame(String game){
        if(game.equals("Free Fire"))return new String[]{"com.dts.freefireth"};
        if(game.equals("Free Fire MAX"))return new String[]{"com.dts.freefiremax"};
        if(game.equals("PUBG MOBILE"))return new String[]{"com.tencent.ig","com.pubg.imobile","com.pubg.krmobile","com.vng.pubgmobile"};
        if(game.equals("COD MOBILE"))return new String[]{"com.activision.callofduty.shooter","com.garena.game.codm"};
        return new String[0];
    }
    void launchSelectedGame(){
        PackageManager pm=getPackageManager();
        for(String pkg:packagesForGame(selectedGame)){
            try{Intent i=pm.getLaunchIntentForPackage(pkg);if(i!=null){i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);startActivity(i);return;}}catch(Exception ignored){}
        }
        Toast.makeText(this,selectedGame+" is not installed on this device",Toast.LENGTH_LONG).show();
    }
    void showLaunch(){
        base();brand("LAUNCH","boost_banner");
        card("✓ RAM READY\n✓ MEMORY OPTIMIZER READY\n✓ GAME PROFILE READY\n✓ PERFORMANCE MODE READY\n\n🎮 GAME: "+selectedGame,CYAN);
        Button opt=btn("🧹  OPTIMIZE MEMORY",PURPLE,PURPLE);content.addView(opt);opt.setOnClickListener(v->showOptimizeToast());
        Button l=btn("🚀  LAUNCH "+selectedGame.toUpperCase(),CYAN,CYAN);l.setTextColor(Color.BLACK);content.addView(l);l.setOnClickListener(v->launchSelectedGame());
        nav();
    }

    void showProfile(){
        base(); brand("PROFILE","profile_banner");
        card("👤 PLAYER\n"+userName+"\n\n🟢 LICENSE STATUS\nACTIVE\n\n🔑 LICENSE KEY\n"+licenseKey,CYAN);
        card("📱 DEVICE\n"+Build.MANUFACTURER+" "+Build.MODEL+"\nAndroid "+Build.VERSION.RELEASE+"\n\nAPP VERSION\n2.2.0",PURPLE);
        metricRow("RAM",ram()+"%","BATTERY",battery()+"%",CYAN);
        card("🔐 SESSION\nYour license session is remembered on this device.\nLogin is only requested again if the saved license becomes invalid or is cleared by the app.",CYAN);
        nav();
    }
    void open(String s){try{startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(s)));}catch(Exception ignored){}}
}
