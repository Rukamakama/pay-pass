package com.example.paypass.view;

public interface IViewActions {
    /**
     * Permet d'interrompre une vue enfin d'appeler la page suivante
     * @param view : la vue qui declance l'action
     */
    void onforwardView (AbstractView view);
}
