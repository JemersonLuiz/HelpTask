package com.example.helpdesk.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.helpdesk.R;
import com.example.helpdesk.models.Chamado;

import java.util.ArrayList;
import java.util.List;

public class ChamadoAdapter extends RecyclerView.Adapter<ChamadoAdapter.ChamadoViewHolder> {
    public interface OnChamadoClickListener {
        void onChamadoClick(Chamado chamado);
    }

    private final List<Chamado> chamados = new ArrayList<>();
    private final OnChamadoClickListener listener;

    public ChamadoAdapter(OnChamadoClickListener listener) {
        this.listener = listener;
    }

    public void atualizar(List<Chamado> novosChamados) {
        chamados.clear();
        if (novosChamados != null) {
            chamados.addAll(novosChamados);
        }
        notifyDataSetChanged();
    }

    @Override
    public ChamadoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chamado, parent, false);
        return new ChamadoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChamadoViewHolder holder, int position) {
        Chamado chamado = chamados.get(position);

        holder.txtTitulo.setText("#" + chamado.getIdChamado() + " - " + chamado.getTitulo());
        holder.txtMeta.setText("Solicitante: " + texto(chamado.getNomeSolicitante()) +
                " | Categoria: " + chamado.getCategoria() +
                " | Prioridade: " + chamado.getPrioridade());
        holder.txtStatus.setText("Status: " + chamado.getStatus());
        holder.txtLocalData.setText("Local: " + chamado.getLocal() +
                " | Aberto em: " + chamado.getDataAbertura());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onChamadoClick(chamado);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chamados.size();
    }

    private String texto(String valor) {
        return valor == null || valor.trim().isEmpty() ? "-" : valor;
    }

    static class ChamadoViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitulo;
        TextView txtMeta;
        TextView txtStatus;
        TextView txtLocalData;

        ChamadoViewHolder(View itemView) {
            super(itemView);
            txtTitulo = itemView.findViewById(R.id.txtItemTitulo);
            txtMeta = itemView.findViewById(R.id.txtItemMeta);
            txtStatus = itemView.findViewById(R.id.txtItemStatus);
            txtLocalData = itemView.findViewById(R.id.txtItemLocalData);
        }
    }
}
