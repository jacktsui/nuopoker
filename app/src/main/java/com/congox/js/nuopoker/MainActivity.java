package com.congox.js.nuopoker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;


public class MainActivity extends Activity {

    Button btnTest;
    ImageView ivNuonuoCard;
    ImageView ivXuCard;
    ImageView ivNuonuo;
    ImageView ivXu;
    Bitmap bmDeck;
    TextView tvPower;
    TextView tvScore;
    Button btnPlay;
    Button btnCompare;
    Button btnAdd;
    Button btnReduce;
    int[] deck;
    static int SPACE = 8;
    static int CARD_WITH = 165;
    static int CARD_HEIGHT = 240;
    static int CARD_LEFT = 68;
    int power = 1;
    int xuCardNo;
    int nuoCardNo;
    int totalScores, score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        readConfig();

        iniApp();
        loadDeck();

        refreshScore();

        busy(true);
        btnTest.bringToFront();

    }

    private void readConfig(){
        SharedPreferences sp = getSharedPreferences("nuocard",MODE_PRIVATE);
        totalScores = sp.getInt("totalScores", 0);

    }


    private  void writeConfig(){
        SharedPreferences sp = getSharedPreferences("nuocard", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("totalScores", totalScores);
        editor.commit();
    }

    private void compare() {
        int nuoValue = nuoCardNo % 13;
        nuoValue = (0 == nuoValue ? 14 : nuoValue);
        int xuValue = xuCardNo % 13;
        xuValue = (0 == xuValue ? 14 : xuValue);

        /*
        Context mContext = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.alertdialog_success,
                (ViewGroup) findViewById(R.id.layoutAlertSuccess));
                */

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("继续玩",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                beginGame();
            }
        });
        builder.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gameOver();
                    }
                });

        if (nuoValue > xuValue) {
            score = 1*power;
            builder.setTitle("你赢了");
            builder.setMessage("恭喜你老婆，你赢了 " + formatScore(score) + " !");
            builder.setIcon(R.drawable.ctestore_success);
            //builder.setView(layout);

        } else if (nuoValue == xuValue) {
            builder.setTitle("平手");
            builder.setMessage("哎呀，咋这么默契呢，平手!");

        } else {
            score = -1 * power;
            builder.setTitle("你输了");
            builder.setMessage("老婆，你输了 " + formatScore(score) + " ，继续加油!");
            builder.setIcon(R.drawable.ctestore_failed);
        }

        builder.show();

        totalScores += score;

        refreshScore();
        //gameOver();

    }

    private  void refreshScore(){
        tvScore.setText(formatScore(totalScores));
    }

    private String formatScore(int score){
        return String.format("$ %,d",score*10000);
    }

    private void beginGame(){
        ivXuCard.setImageBitmap(null);
        ivNuonuoCard.setImageBitmap(null);

        btnPlay.setVisibility(View.INVISIBLE);

        int n = getRestCardsCount();
        //if (0 == n){
         //   loadDeck();
        //}
        xuCardNo = (int)(Math.random()*n);
        final ImageView iv = (ImageView)findViewById(deck[xuCardNo]);
        removeCard(xuCardNo);

        AnimationSet animationSet = new AnimationSet(true);
        TranslateAnimation ta = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
               Animation.ABSOLUTE, -iv.getLeft(),
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.ABSOLUTE, -iv.getTop());
        ta.setDuration(2000);
        animationSet.addAnimation(ta);

        iv.startAnimation(animationSet);

        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ivNuonuoCard.setImageBitmap(null);
                busy(true);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ivXuCard.setImageResource(R.drawable.cardbg);
                iv.setVisibility(View.INVISIBLE);

                busy(false);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void gameOver(){
        btnPlay.setVisibility(View.VISIBLE);
        btnPlay.bringToFront();
        busy(true);
    }

    private void iniApp(){
        btnTest = (Button)findViewById(R.id.btnTest);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginGame();
                btnTest.setVisibility(View.INVISIBLE);
            }
        });

        ivNuonuo = (ImageView)findViewById(R.id.ivNuonuo);
        ivNuonuo.setImageResource(R.drawable.nuonuo);
        ivXu = (ImageView)findViewById(R.id.ivXu);
        ivXu.setImageResource(R.drawable.xu);


        ivXuCard = (ImageView)findViewById(R.id.ivXucard);
        ivNuonuoCard = (ImageView)findViewById(R.id.ivNuoCard);

        Resources r =  this.getResources();
        InputStream is = r.openRawResource(R.drawable.deck);
        BitmapDrawable bmpDraw = new BitmapDrawable(is);
        bmDeck = bmpDraw.getBitmap();

        tvPower = (TextView)findViewById(R.id.tvPower);
        tvScore = (TextView)findViewById(R.id.tvScore);

        btnPlay = (Button)findViewById(R.id.btnPlay);
        btnPlay.setVisibility(View.INVISIBLE);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginGame();
            }
        });

        btnCompare = (Button)findViewById(R.id.btnCompare);
        btnCompare.setVisibility(View.INVISIBLE);
        btnCompare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCard(false);
                showCard(true);

                compare();
                btnCompare.setVisibility(View.INVISIBLE);

            }
        });

        btnAdd = (Button)findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //power = (int)(tvPower.getTag());
                if (power < 9){
                    power++;
                    tvPower.setText("x"+power);
                }else{
                    Toast.makeText(getApplicationContext(),"你加的码已经够大了，败家娘们！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnReduce = (Button)findViewById(R.id.btnReduce);
        btnReduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (power > 1){
                    power--;
                    tvPower.setText("x"+power);
                }else {
                    Toast t = Toast.makeText(getApplicationContext(),"不能再减了，亲爱的！", Toast.LENGTH_SHORT);
                    t.show();
                }

            }
        });

    }

   private void sendCard(){

       final ImageView iv = (ImageView)findViewById(nuoCardNo);
       removeCard(nuoCardNo);

       AnimationSet animationSet = new AnimationSet(true);
       TranslateAnimation ta = new TranslateAnimation(
               Animation.RELATIVE_TO_SELF, 0f,
               Animation.ABSOLUTE, -iv.getLeft(),
               Animation.RELATIVE_TO_SELF, 0f,
               Animation.ABSOLUTE, iv.getTop());
       ta.setDuration(2000);
       animationSet.addAnimation(ta);

       iv.startAnimation(animationSet);
       animationSet.setAnimationListener(new Animation.AnimationListener() {
           @Override
           public void onAnimationStart(Animation animation) {
               busy(true);
           }

           @Override
           public void onAnimationEnd(Animation animation) {
               ivNuonuoCard.setImageResource(R.drawable.cardbg);
               btnCompare.setVisibility(View.VISIBLE);
               btnCompare.bringToFront();
               iv.setVisibility(View.INVISIBLE);
               //busy(false);
           }

           @Override
           public void onAnimationRepeat(Animation animation) {

           }
       });

   }

    private void showCard(boolean me){
        int n = me?nuoCardNo:xuCardNo;
        int x = n % 13;
        int y = n / 13;
        Bitmap bmCard = Bitmap.createBitmap(bmDeck, x*CARD_WITH, y*CARD_HEIGHT, CARD_WITH, CARD_HEIGHT);
        if (me){
            ivNuonuoCard.setImageBitmap(bmCard);
        }else{
            ivXuCard.setImageBitmap(bmCard);
        }

    }

    private void loadDeck(){
        int i,j, n=0;

        deck = new int[52];

        for (i=0; i<13; i++){
            for (j=0; j<4; j++){
                deck[n] = n;

                ImageView iv = new ImageView(this);
                iv.setId(n);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nuoCardNo = v.getId();
                        sendCard();
                    }
                });
                iv.setImageResource(R.drawable.cardbg);
                RelativeLayout layout = (RelativeLayout)findViewById(R.id.relativeLayout);
                RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp1.addRule(RelativeLayout.CENTER_VERTICAL);
                lp1.leftMargin = CARD_LEFT + n*SPACE;

                layout.addView(iv, lp1);
                n++;
            }
        }


    }

    private void busy(boolean isBusy){
        //int n = getRestCardsCount();
        ImageView iv;
        for(int i=0;i<52;i++){
            iv = (ImageView)findViewById(i);
            iv.setEnabled(!isBusy);
        }
    }

    private void removeCard(int pos){
        int c = getRestCardsCount();
        for (int i=pos;i<c-2;i++){
            deck[i] = deck[i+1];
        }
        deck[c-1] = -1;
    }


    private int getRestCardsCount(){
        int n = 0;
        for (int i=0;i<52;i++){
            if (-1 == deck[i])
               break;
            n++;
        }
        return n;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        writeConfig();
    }
}
