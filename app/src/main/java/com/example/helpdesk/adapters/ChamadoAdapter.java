package com.example.helpdesk.adapters;

import android.view.LayoutInflater;
import android.content.res.ColorStateList;
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

        holder.txtTitulo.setText(holder.itemView.getContext().getString(R.string.item_titulo,
                chamado.getIdChamado(), chamado.getTitulo()));
        holder.txtMeta.setText(holder.itemView.getContext().getString(R.string.item_categoria,
                chamado.getCategoria(), chamado.getPrioridade()));
        holder.txtStatus.setText(chamado.getStatus());
        int fundo = R.color.primary_light;
        int texto = R.color.primary_dark;
        switch (chamado.getStatus()) {
            case "Em andamento":
                fundo = R.color.andamento_fundo;
                texto = R.color.andamento_texto;
                break;
            case "Resolvido":
                fundo = R.color.resolvido_fundo;
                texto = R.color.resolvido_texto;
                break;
            case "Cancelado":
                fundo = R.color.background;
                texto = R.color.text_secondary;
                break;
        }
        holder.txtStatus.setBackgroundTintList(ColorStateList.valueOf(holder.itemView.getContext().getColor(fundo)));
        holder.txtStatus.setTextColor(holder.itemView.getContext().getColor(texto));
        holder.txtLocalData.setText(holder.itemView.getContext().getString(R.string.item_origem,
                chamado.getLocal(), texto(chamado.getNomeSolicitante()), chamado.getDataAbertura()));

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
