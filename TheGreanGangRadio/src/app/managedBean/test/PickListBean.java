package app.managedBean.test;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.DualListModel;


@ManagedBean
@ViewScoped
public class PickListBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5342717537720776925L;
	// Modifier this class (DualListModel) ;
	private DualListModel<String> songs;
	
	public PickListBean() {
		List<String> sourceSong = new ArrayList<String>();
		List<String> targetSong = new ArrayList<String>();
		sourceSong.add("aaa");
		sourceSong.add("bbb");
		sourceSong.add("ccc");
		sourceSong.add("ddd");
		sourceSong.add("eee");
		
		songs = new DualListModel<String>(sourceSong, targetSong);
	}
 
	public DualListModel<String> getSongs() {
		return songs;
	}
	
	public void setSongs(DualListModel<String> songs) {
		this.songs = songs;
	}
}

