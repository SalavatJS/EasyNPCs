package family.salavat.easynpcs;

import family.salavat.easynpcs.models.NpcModel;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class NpcRepository {

    private FileConfiguration configuration;

    private File file;

    private Map<Integer, NpcModel> models;

    public NpcRepository(FileConfiguration configuration, File file) {
        this.configuration = configuration;
        this.file = file;
        models = new HashMap<>();
        configuration.getKeys(false).forEach(id -> {
            Bukkit.broadcastMessage(id);
            NpcModel model = (NpcModel) configuration.get(id);
            models.put(Integer.parseInt(id.substring(2)), model);
        });
    }

    public NpcModel get(int id) {
        return models.get(id);
    }

    public int put(NpcModel model) {
        int id = 1;
        while (models.containsKey(id)) id++;
        model.setId(id);
        models.put(id, model);
        configuration.set("id" + id, model);
        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return id;
    }

    public void save() {
        models.forEach((id, model) -> configuration.set("id" + id, model));
        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void remove(int id) {
        models.remove(id);
        configuration.set("id" + id, null);
        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<Integer, NpcModel> getModels() {
        return models;
    }

}
