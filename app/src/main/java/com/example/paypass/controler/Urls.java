package com.example.paypass.controler;

/**
 * Interface pour stoquer l'ensemble des url de l'api
* */

interface Urls {

    String HOST = "http://paypassapi.lifti-paypass.com/paypassApi/public/api/";
    String LOGIN_AGENT = HOST+"agent/";
    String CREATE_CLIENT = HOST+"client/update/";
    String LOGIN_CLIENT = HOST+"client/";
    String LOGIN_ABONNE = HOST+"abonnes/";
    String LOGIN_BUS = HOST+"bus/";
    String RECHARGE_CLIENT = HOST+"recharge/";
    String HIST_RECHARGE = HOST+"historique/recharge";
    String HIST_RETRAIT = HOST+"historique/retrait";
    String SOLDE_BUS_CL = HOST+"busClient/getsoldes";
    String SOLDE_AGT_CL = HOST+"agentClients/getsoldes";
    String SOLDE_AGT_ABN = HOST+"agentAbonnes/getsoldes";
    String PAY_BUS = HOST+"payement/";
    String ABONNEMENT = HOST+"abonnement";
    String CLIENT_ACTIVE =  HOST+"client/active/";

}
