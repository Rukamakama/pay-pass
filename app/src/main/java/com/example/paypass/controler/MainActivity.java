package com.example.paypass.controler;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.paypass.R;
import com.example.paypass.model.*;
import com.example.paypass.view.*;

import java.util.Map;

import static com.example.paypass.view.IViewIds.*;

public class MainActivity extends AppCompatActivity implements IViewActions, Controler.ControlerEvents {

    public static final byte ID_AGENT = 2;
    public static final byte ID_CLIENT = 3;
    public static final byte ID_ABONNE = 4;
    public static final byte ID_BUS = 5;
    private AbstractView vuePrincipale;
    private Card card;
    private byte appType;
    private byte qr_owner;
    private Map<String, String> inputDataMap;
    private double montant;
    private boolean isAbonne = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.appType = ID_BUS;
        card = null;
        setView(new HomeView(this, appType));
    }

    @Override
    public void onforwardView(AbstractView view) {

        if (view instanceof HomeView) {
            setView(new LoginView(this, appType));// Go to login page
        } else if (view instanceof LoginView) {
            if (view.getRequestedAction() == QR_VIEW) {
                //On prend d'abord les informations entrees
                LoginView loginView = (LoginView) view;
                inputDataMap = loginView.getMapInfo();
                qr_owner = loginView.getMode();
                isAbonne = loginView.isAbonne();
                setView(new ScanQrcodeView(this, appType));
            }
        } else if (view instanceof ScanQrcodeView) {
            String qr_code = ((ScanQrcodeView) view).getQrCode(); //On recuper le qr_code
            final CardControler cardControler;
            final OperationControler opeControler;
            final Operation operation; //Pour les opérations
            switch (qr_owner) {
                case LOGIN_AGENT:
                    String codeAgent = inputDataMap.get(getString(R.string.strCode)); //On recupere le code de l'agent
                    card = new Agent(qr_code, codeAgent);
                    cardControler = new LoginControler(this, card);
                    ((LoginControler) cardControler).setLoginMode(ID_AGENT);
                    cardControler.proces(); //Connexion de l'agent
                    break;
                case LOGIN_BUS:
                    String codeBus = inputDataMap.get(getString(R.string.strCode)); //On recupere le code du bus
                    card = new Bus(qr_code, codeBus);
                    cardControler = new LoginControler(this, card);
                    ((LoginControler) cardControler).setLoginMode(ID_BUS);
                    cardControler.proces(); //Connexion du bus
                    break;
                case LOGIN_CLIENT:
                    if(isAbonne){
                        card = new Abonne(qr_code,"");
                        cardControler = new LoginControler(this, card);
                        ((LoginControler) cardControler).setLoginMode(ID_ABONNE);
                    }else {
                        card = new Client(qr_code, "");
                        cardControler = new LoginControler(this, card);
                        ((LoginControler) cardControler).setLoginMode(ID_CLIENT);
                    }
                    cardControler.proces(); //Connexion du client
                    break;
                case CREATION_CLIENT:
                    Client client = new Client(qr_code,
                            inputDataMap.get(getString(R.string.strCode)),
                            inputDataMap.get(getString(R.string.strName)),
                            card.getQr_code()); //Creation du client par l'agent
                    cardControler = new NewClientControler(this, client);
                    cardControler.proces(); //Creation du nouveau client
                    break;
                case RECHARGE_CLIENT: //La carte cliente peut etre recharge
                    if(isAbonne){
                        operation = new Abonnement(card, new Abonne(qr_code, ""));
                        operation.setMontant(montant);
                        opeControler = new AbonnementController(this, operation);
                    }else {
                        operation = new Recharge(card, new Client(qr_code, ""));
                        operation.setMontant(montant);
                        opeControler = new RechargeController(this, operation);
                    }
                    opeControler.proces();
                    break;
                case PAYEMENT_BUS: //Le bus est pret à recevoir le payement
                    operation = new Payement(card, new Client(qr_code, ""));
                    operation.setMontant(montant);
                    opeControler = new PayementControler(this, operation);
                    opeControler.proces();
                    break;
            }
        } else if (view instanceof OperationAgentView) {
            switch (view.getRequestedAction()) {
                case RECHARGE_CLIENT:
                    setView(new RechargeView(this, appType));
                    break;
                case COMPTE_VIEW:
                    setView(new CompteView(this, appType, card));
                    break;
                case CREATION_CLIENT:
                    setView(new LoginView(this, appType, true));
                    break;
            }
        } else if (view instanceof CompteView) {
            HistoriqueControler controler;
            switch (view.getRequestedAction()) {
                case HIST_RETRAIT_AG:
                    controler = new HistoriqueControler(this, HistoriqueView.HIST_RETR);
                    controler.proces();
                    break;
                case HIST_RECHARGE_AG:
                    controler = new HistoriqueControler(this, HistoriqueView.HIST_RECH);
                    controler.proces();
                    break;
                case QR_VIEW: //Il s'agit du compte d'un bus et le client veut payer
                    qr_owner = PAYEMENT_BUS; //Pour le payment du client
                    montant = ((CompteView)view).getPrice();
                    setView(new ScanQrcodeView(this, appType));
                    break;
                case HOME_VIEW:
                    setView(new HomeView(this, appType));
                    break;
            }
        } else if (view instanceof RechargeView) {
            if (view.getRequestedAction() == QR_VIEW) {
                qr_owner = RECHARGE_CLIENT;
                RechargeView rechargeView = (RechargeView)vuePrincipale;
                montant = rechargeView.getMontant();
                isAbonne = rechargeView.isAbonne();
                setView(new ScanQrcodeView(this, appType));
            }

        }
    }

    private void setView(AbstractView view) {
        vuePrincipale = view;
        setContentView(vuePrincipale.getRootView());
    }

    @Override
    public void onProcessFinish(Controler controler, Card card) {
        if (controler instanceof LoginControler){
            if (controler.isDone()){
                Toast.makeText(this, getString(R.string.strLoginOk), Toast.LENGTH_SHORT).show();
                this.card = card;
                if (appType == ID_AGENT)
                    setView(new OperationAgentView(this, appType, card)); //agent page
                else
                    setView(new CompteView(this, appType, card)); //compte or client page
            }else
                setView(new LoginView(this, appType));
        }else if(controler instanceof  PayementControler){
            qr_owner = PAYEMENT_BUS;
            if (controler.isDone())
                this.card = card;
            setView(new ScanQrcodeView(this, appType));
        }else if((controler instanceof  RechargeController) ||
                (controler instanceof  AbonnementController)){
            if (controler.isDone())
                this.card = card;
            setView(new RechargeView(this, appType));
        }else if(controler instanceof NewClientControler){
            setView(new OperationAgentView(this, appType,this.card));
        }else if(controler instanceof HistoriqueControler){
            HistoriqueControler hisCtrl = (HistoriqueControler)controler;
            switch (hisCtrl.getType()){
                case HistoriqueView.HIST_RECH:
                    setView(new HistoriqueView(this, appType, HistoriqueView.HIST_RECH));
                    ((HistoriqueView) vuePrincipale).setData(hisCtrl.getTabData());
                    break;
                case HistoriqueView.HIST_RETR:
                    setView(new HistoriqueView(this, appType));
                    ((HistoriqueView) vuePrincipale).setData(hisCtrl.getTabData());
                    break;
            }
        }
    }

    @Override
    public void errorDialog(String message, final byte view_id) {
        final InfoDialog dialogBox = new InfoDialog(this, getString(R.string.strErrorMsg),
                message, InfoDialog.YES) {
            @Override
            public void onExitDialog(byte action) {
                switch (view_id){
                    case LOGIN_VIEW :
                        setView(new LoginView(MainActivity.this, appType));
                        break;
                    case PAYEMENT_BUS :
                        qr_owner = PAYEMENT_BUS; //Pour le payment du client
                        setView(new ScanQrcodeView(MainActivity.this, appType));
                        break;
                    case CREATION_CLIENT :
                        setView(new LoginView(MainActivity.this, appType, true));
                        break;
                    case RECHARGE_CLIENT :
                        setView(new RechargeView(MainActivity.this, appType));
                }
            }
        };
        dialogBox.showDialog();

    }

    @Override
    public void onBackPressed() {
        if (vuePrincipale instanceof LoginView){
            if (((LoginView)vuePrincipale).getMode() == CREATION_CLIENT)
                setView(new OperationAgentView(this, appType, card));
            else
                setView(new HomeView(this,appType));
        }else if(vuePrincipale instanceof CompteView){
            if (appType == ID_AGENT)
                setView(new OperationAgentView(this, appType, card));
            else
                setView(new LoginView(this,appType));
        }else if(vuePrincipale instanceof ScanQrcodeView){
            switch (qr_owner){
                case LOGIN_AGENT :
                case LOGIN_BUS :
                case LOGIN_CLIENT :
                    setView(new LoginView(this,appType));
                    break;
                case CREATION_CLIENT :
                    setView(new LoginView(this, appType, true));
                    break;
                case RECHARGE_CLIENT :
                    setView(new RechargeView(this, appType));
                    break;
                case PAYEMENT_BUS :
                    setView(new CompteView(this,appType,card));
                    break;
            }
        }else if(vuePrincipale instanceof OperationAgentView){
            setView(new LoginView(this,appType));
        }else if(vuePrincipale instanceof RechargeView){
            setView(new OperationAgentView(this, appType, card));
        }else if(vuePrincipale instanceof HistoriqueView){
            setView(new CompteView(this, appType, card));
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        switch (appType) {
            case ID_AGENT:
                inflater.inflate(R.menu.menu_agent, menu);
                break;
            case ID_CLIENT:
                inflater.inflate(R.menu.menu_client, menu);
                break;
            case ID_BUS :
                inflater.inflate(R.menu.menu_bus, menu);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home : onBackPressed();
                return true;
            case R.id.menuContact :
                new InfoDialog(this, getString(R.string.strContact),
                        getString(R.string.strDetailContact), InfoDialog.YES) {
                    @Override
                    public void onExitDialog(byte action) {}
                }.showDialog();
                return true;
            case R.id.menuAide :
                String message = "";
                switch (appType){
                    case ID_AGENT : message = getString(R.string.strAideAgent);
                        break;
                    case ID_BUS : message = getString(R.string.strAideBus);
                        break;
                    case ID_CLIENT : message = getString(R.string.strAideClient);
                        break;
                }
                new InfoDialog(this, getString(R.string.strHelp),
                        message, InfoDialog.YES) {
                    @Override
                    public void onExitDialog(byte action) {}
                }.showDialog();
                return true;
            case R.id.menuDeconnect :
                setView(new HomeView(this,appType));
                return true;
            default :
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (vuePrincipale instanceof ScanQrcodeView)
            ((ScanQrcodeView) vuePrincipale).startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (vuePrincipale instanceof ScanQrcodeView)
            ((ScanQrcodeView) vuePrincipale).stopCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Pour raison de securite on ferme l'application si elle n'est plus visible a l'ecrant
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (vuePrincipale instanceof ScanQrcodeView)
            ((ScanQrcodeView) vuePrincipale).stopCamera();
    }

    public void setToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //TODO : Change icons color to gray
    //TODO : Remove back arrow partially(some views) or complitly
    //TODO : Remove the border
    //TODO : Add a voice notification for payment
    //TODO : Remove toolbar for home page
    //TODO : Add a splash view
    //TODO : Add camera request programmatically
    //TODO : Update logo
    //TODO : change "a paye" sound to Ok for "abonnees"

}
