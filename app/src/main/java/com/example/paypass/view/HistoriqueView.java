package com.example.paypass.view;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.example.paypass.R;

public class HistoriqueView extends AbstractView {

    public static final byte HIST_RETR = 1;
    public static final byte HIST_RECH = 3;
    public static int COLOMN_COUNT = 3;
    public static final int ROW_COUNT = 8;
    private static final int MAX_PAGE_COUNT = 5;
    private int numPage;
    private String[][] tabData;

    private AppCompatTextView[][] tabDisplayTv;
    private AppCompatImageButton btnSuivant;
    private AppCompatImageButton btnPrecedent;

    private byte type;

    public HistoriqueView(Context context, byte appType) {
        super(context, R.layout.historique, appType);
        this.type = HIST_RETR;
        initComponents();
    }

    public HistoriqueView(Context context, byte appType, byte type) {
        super(context, R.layout.historique_recharge, appType);
        this.type = type;
        initComponents();
    }

    @Override
    protected void initComponents() {
        AppCompatButton btnDate = null;
        TableLayout tablyt = null;
        AppCompatTextView tvTitle;
        switch (type){
            case HIST_RETR:
                tvTitle = rootView.findViewById(R.id.tvTitle_hist);
                tvTitle.setText(context.getString(R.string.strHistoriqueRetr));
                btnDate = rootView.findViewById(R.id.btnHDate);
                btnSuivant = rootView.findViewById(R.id.btnSuivRetr);
                btnPrecedent = rootView.findViewById(R.id.btnPrecRetr);
                tablyt = rootView.findViewById(R.id.tableRetrait);
                break;
            case HIST_RECH:
                tvTitle = rootView.findViewById(R.id.tvTitle_hist_rech);
                tvTitle.setText(context.getString(R.string.strHistoriqueRech));
                btnDate = rootView.findViewById(R.id.btnHDateRech);
                btnSuivant = rootView.findViewById(R.id.btnSuivRech);
                btnPrecedent = rootView.findViewById(R.id.btnPrecRech);
                COLOMN_COUNT = 4;
                tablyt = rootView.findViewById(R.id.tableRecharge);
                break;
        }
        tabDisplayTv = new AppCompatTextView[ROW_COUNT][COLOMN_COUNT];
        if (btnDate != null && tablyt != null){
            btnDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Launch date picker
                }
            });
            for (int i=0; i<ROW_COUNT; i++){
                TableRow row = (TableRow)tablyt.getChildAt(i+1);//les lignes des donnees

                for (int j=0; j< row.getChildCount(); j++){
                    tabDisplayTv[i][j] = (AppCompatTextView)row.getChildAt(j);
                }
            }
        }
        btnPrecedent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pagePrecedent();
            }
        });
        btnSuivant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageSuivant();
            }
        });
        btnPrecedent.setClickable(false);
        btnSuivant.setClickable(false);
    }

    private void pageSuivant(){
        if (numPage <= MAX_PAGE_COUNT){

            int nbrLigne = tabData.length;
            int cursorPosition = (numPage-1)*ROW_COUNT;

            if (nbrLigne <= numPage*ROW_COUNT){
                String[][] tab = new String[nbrLigne-cursorPosition][COLOMN_COUNT];
                System.arraycopy(tabData,cursorPosition,tab,0,
                        nbrLigne-cursorPosition);
                setContent(tab);
                btnSuivant.setClickable(false);
            }
            else {
                String[][] tab = new String[nbrLigne-cursorPosition][COLOMN_COUNT];
                System.arraycopy(tabData,cursorPosition,tab,0,ROW_COUNT);
                setContent(tab);
                if (numPage == MAX_PAGE_COUNT) btnSuivant.setClickable(false);
                else numPage++;
            }
            if (numPage > 1) btnPrecedent.setClickable(true);
        }
    }

    private void pagePrecedent(){
        if (numPage > 1){
            numPage--;
            int cursorPosition = (numPage-1)*ROW_COUNT;
            String[][] tab = new String[ROW_COUNT][COLOMN_COUNT];
            System.arraycopy(tabData,cursorPosition,tab,0,ROW_COUNT);
            setContent(tab);
            btnSuivant.setClickable(true);
            if (numPage == 1) btnPrecedent.setClickable(false);
        }
    }

    private void setContent(String[][] tabData){
        for(AppCompatTextView[] row : tabDisplayTv){
            //On efface tous textView
            for(AppCompatTextView tv : row){
                tv.setText("");
            }
        }
        for (int i=0; i<tabData.length; i++){
            for (int j=0; j<tabData[i].length; j++){
                tabDisplayTv[i][j].setText(tabData[i][j]);
            }
        }
    }

    public void setData(String[][] tabData){
        numPage = 1;
        this.tabData = tabData;
        if(tabData.length > ROW_COUNT)
            btnSuivant.setClickable(true);
        pageSuivant();
    }
}
