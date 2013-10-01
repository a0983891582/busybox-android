package stericson.busybox.donate.adapter;

import stericson.busybox.donate.App;
import stericson.busybox.donate.Constants;
import stericson.busybox.donate.R;
import stericson.busybox.donate.activities.MainActivity;
import stericson.busybox.donate.custom.FontableTextView;
import stericson.busybox.donate.jobs.containers.Item;
import stericson.busybox.donate.listeners.AppletLongClickListener;
import stericson.busybox.donate.listeners.Location;
import stericson.busybox.donate.listeners.Version;
import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class TuneAdapter extends BaseAdapter {
    private int[] colors = new int[]{0xff303030, 0xff404040};
    private MainActivity activity;
    private static LayoutInflater inflater = null;
    private CheckBox all;

    private int version_index = 0;
    private int path_index = 0;

    public TuneAdapter(MainActivity activity) {
        this.activity = activity;

        inflater = (LayoutInflater) activity
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        App.getInstance().setTuneadapter(this);
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    private class InformationHolder {
        private FontableTextView foundAt;
        private LinearLayout options;
        private FontableTextView status;
        private LinearLayout container;
        private CheckBox clean;
        private FontableTextView freeSpace;
        private Spinner version;
        private Spinner path;
    }

    private class ViewHolder {
        private RelativeLayout container;
        private CheckBox appletCheck;
        private CheckBox appletDecision;
        private TextView appletState;
        private TextView appletStatus;
        private TextView appletSymlinkedTo;
        private TextView appletRecomendation;
    }

    private ViewHolder getHolder(View convertView, int position) {
        if (convertView == null) {
            if (position != 0) {
                return new ViewHolder();
            }
        } else {
            if (convertView.getTag() != null) {
                if (position != 0) {
                    return ((ViewHolder) convertView.getTag());
                }
            }
        }

        return null;
    }

    private InformationHolder getInformationHolder(View convertView,
                                                   int position) {
        if (convertView == null) {
            if (position == 0) {
                return new InformationHolder();
            }
        } else {
            if (convertView.getTag() != null) {
                if (position == 0) {
                    return ((InformationHolder) convertView.getTag());
                }
            }
        }

        return null;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final App app = App.getInstance();
        final ViewHolder holder = getHolder(convertView, position);
        final InformationHolder info_holder = getInformationHolder(convertView,
                position);

        if (convertView == null) {
            if (position == 0) {
                vi = inflater.inflate(R.layout.main_content, null);

                //Set everything up
                info_holder.foundAt = (FontableTextView) vi.findViewById(R.id.foundat);
                info_holder.version = (Spinner) vi.findViewById(R.id.busyboxversiontobe);
                info_holder.path = (Spinner) vi.findViewById(R.id.path);
                info_holder.freeSpace = (FontableTextView) vi.findViewById(R.id.freespace);
                info_holder.container = (LinearLayout) vi.findViewById(R.id.custom_container);
                info_holder.status = (FontableTextView) vi.findViewById(R.id.customtune_status);
                info_holder.options = (LinearLayout) vi.findViewById(R.id.smart_install_options);
                info_holder.clean = (CheckBox) vi.findViewById(R.id.clean_all);
                all = (CheckBox) vi.findViewById(R.id.sym_all);

                //Assign values, turn things on, hide what we don't need.
                info_holder.foundAt.setText(app.getFound());

                ArrayAdapter<String> versionAdapter = new ArrayAdapter<String>(activity, R.layout.simple_spinner_item, Constants.versions);
                versionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                info_holder.version.setAdapter(versionAdapter);
                info_holder.version.setOnItemSelectedListener(new Version(activity));
                info_holder.version.setSelection(version_index);

                ArrayAdapter<String> locationAdapter = new ArrayAdapter<String>(activity, R.layout.simple_spinner_item, Constants.locations);
                locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                info_holder.path.setAdapter(locationAdapter);
                info_holder.path.setOnItemSelectedListener(new Location(activity));
                info_holder.path.setSelection(path_index);

                info_holder.freeSpace.setText(App.getInstance().getSpace() != -1 ? activity.getString(R.string.amount) + " " + (activity.getCustomPath().equals("") ? "/system" : activity.getCustomPath()) + " " + App.getInstance().getSpace() + "mb" : "");

                info_holder.container.setVisibility(app.getItemList() != null ? View.GONE : View.VISIBLE);

                info_holder.status.setVisibility(app.getItemList() != null ? (!app.isToggled() ? View.VISIBLE : View.GONE) : View.VISIBLE);
                info_holder.status.setText(app.getItemList() != null ? R.string.advanced : R.string.smart_install_loading);

                info_holder.options.setVisibility((app.getItemList() != null ? (app.isToggled() ? View.VISIBLE : View.GONE) : View.GONE));

                all.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (buttonView.isPressed()) {
                            for (Item item : app.getItemList()) {
                                item.setOverwriteAll(isChecked);
                            }

                            TuneAdapter.this.notifyDataSetChanged();
                        }
                    }
                });

                info_holder.clean
                        .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                            public void onCheckedChanged(
                                    CompoundButton buttonView, boolean isChecked) {
                                if (isChecked)
                                    activity.initiatePopupWindow(
                                            "This feature will instruct the installer to make sure that applets are not duplicated. This will keep your system cleaner, save more space, and will ensure that other applications do not have problems using the applets provided by Busybox. \n\n Please be very careful when using this option as it can cause system problems if the applets removed by using this option are required by your system. \n\n THIS IS AN ADVANCED OPTION, USE WITH CARE!",
                                            false, activity);

                                activity.setClean(isChecked);
                            }
                        });

                vi.setTag(info_holder);
            } else {
                if (app.getItemList() != null && app.getItemList().size() > 0) {
                    vi = inflater.inflate(R.layout.list_item, null);

                    holder.container = (RelativeLayout) vi
                            .findViewById(R.id.container);
                    holder.container.setClickable(true);
                    holder.container
                            .setOnLongClickListener(new AppletLongClickListener(
                                    this.activity));

                    holder.appletCheck = (CheckBox) vi
                            .findViewById(R.id.appletCheck);

                    holder.appletDecision = (CheckBox) vi
                            .findViewById(R.id.appletDecision);

                    holder.appletState = (TextView) vi
                            .findViewById(R.id.appletState);

                    holder.appletStatus = (TextView) vi
                            .findViewById(R.id.appletStatus);

                    holder.appletSymlinkedTo = (TextView) vi
                            .findViewById(R.id.appletSymlinkedTo);

                    holder.appletRecomendation = (TextView) vi
                            .findViewById(R.id.appletRecommendation);

                    vi.setTag(holder);
                }
            }
        }

        if (position == 0) {
            //Update
            info_holder.container.setVisibility(app.getItemList() != null ? View.GONE : View.VISIBLE);

            info_holder.freeSpace.setText(App.getInstance().getSpace() != -1 ? activity.getString(R.string.amount) + " " + (activity.getCustomPath().equals("") ? "/system" : activity.getCustomPath()) + " " + App.getInstance().getSpace() + "mb" : "");

            info_holder.foundAt.setText(App.getInstance().getFound());

            info_holder.options.setVisibility((app.getItemList() != null ? (app.isToggled() ? View.VISIBLE : View.GONE) : View.GONE));

            info_holder.status.setVisibility(app.getItemList() != null ? (!app.isToggled() ? View.VISIBLE : View.GONE) : View.VISIBLE);
            info_holder.status.setText(app.getItemList() != null ? R.string.advanced : R.string.smart_install_loading);

            info_holder.version.setSelection(version_index);
            info_holder.path.setSelection(path_index);

        } else {
            //update
            final int modified_position = position - 1;

            if (app.getItemList() != null && app.getItemList().size() > 0) {

                final Item item = app.getItemList().get(modified_position);

                holder.appletDecision
                        .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                            public void onCheckedChanged(
                                    CompoundButton buttonView, boolean isChecked) {
                                item.setRemove(!isChecked);

                                if (isChecked) {
                                    holder.appletDecision
                                            .setText(R.string.leaveApplet);
                                    holder.appletDecision
                                            .setTextColor(Color.GREEN);
                                } else {
                                    holder.appletDecision
                                            .setText(R.string.removeApplet);
                                    holder.appletDecision
                                            .setTextColor(Color.RED);
                                }
                            }
                        });

                holder.appletCheck.setText(item.getApplet());
                holder.appletCheck
                        .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                            public void onCheckedChanged(
                                    CompoundButton buttonView, boolean isChecked) {
                                if (buttonView.isPressed()) {
                                    all.setChecked(false);
                                    item.setOverwrite(isChecked);

                                    if (isChecked) {
                                        if (!App.getInstance().getAvailableApplets().contains(item.getApplet())) {
                                            holder.appletState.setText(R.string.binary_notavail);
                                            holder.appletState.setTextColor(Color.RED);
                                        } else {
                                            holder.appletState.setText(R.string.willSymlink);
                                            holder.appletState.setTextColor(Color.GREEN);
                                        }

                                        holder.appletDecision.setVisibility(View.GONE);
                                    } else {
                                        if (!App.getInstance().getAvailableApplets().contains(item.getApplet())) {
                                            holder.appletState.setText(R.string.binary_notavail);
                                        } else {
                                            holder.appletState.setText(R.string.willNotSymlink);
                                        }
                                        holder.appletState.setTextColor(Color.RED);

                                        holder.appletDecision.setVisibility(View.VISIBLE);

                                        if (item.getRemove()) {
                                            holder.appletDecision.setText(R.string.removeApplet);
                                            holder.appletDecision.setTextColor(Color.RED);
                                            holder.appletDecision.setChecked(false);
                                        } else {
                                            holder.appletDecision.setText(R.string.leaveApplet);
                                            holder.appletDecision.setTextColor(Color.GREEN);
                                            holder.appletDecision.setChecked(true);
                                        }

                                    }
                                }
                            }
                        });

                if (!App.getInstance().getAvailableApplets().contains(item.getApplet())) {
                    holder.appletCheck.setChecked(false);
                    holder.appletCheck.setEnabled(false);
                } else {
                    holder.appletCheck.setEnabled(true);
                    holder.appletCheck.setChecked(item.getOverwriteall() ? true : item.getOverWrite());
                }

                if (holder.appletCheck.isChecked()) {
                    holder.appletState.setText(R.string.willSymlink);
                    holder.appletState.setTextColor(Color.GREEN);
                    holder.appletDecision.setVisibility(View.GONE);
                } else {
                    if (!App.getInstance().getAvailableApplets().contains(item.getApplet())) {
                        holder.appletState.setText(R.string.binary_notavail);
                    } else {
                        holder.appletState.setText(R.string.willNotSymlink);
                    }

                    holder.appletState.setTextColor(Color.RED);
                    holder.appletDecision.setVisibility(View.VISIBLE);

                    if (item.getRemove()) {
                        holder.appletDecision.setText(R.string.removeApplet);
                        holder.appletDecision.setTextColor(Color.RED);
                        holder.appletDecision.setChecked(false);
                    } else {
                        holder.appletDecision.setText(R.string.leaveApplet);
                        holder.appletDecision.setTextColor(Color.GREEN);
                        holder.appletDecision.setChecked(true);
                    }
                }

                if (item.getFound()) {
                    if (!item.getSymlinkedTo().equals("")) {
                        holder.appletSymlinkedTo.setVisibility(View.VISIBLE);
                        holder.appletStatus.setText(R.string.symlinked);
                        holder.appletSymlinkedTo.setText(activity
                                .getString(R.string.symlinkedTo)
                                + " "
                                + item.getSymlinkedTo());
                    } else {
                        holder.appletSymlinkedTo.setVisibility(View.GONE);
                        holder.appletStatus.setText(R.string.hardlinked);
                    }
                } else {
                    holder.appletStatus.setText(R.string.notFound);
                    holder.appletSymlinkedTo.setText(activity
                            .getString(R.string.notsymlinked));
                }

                if (item.getRecommend()) {
                    holder.appletRecomendation.setText(R.string.cautionDo);
                } else {
                    holder.appletRecomendation.setText(R.string.cautionDoNot);
                }

                if (position % 2 == 0) {
                    holder.container.setBackgroundColor(colors[position % 2]);
                } else {
                    holder.container.setBackgroundColor(colors[position % 2]);
                }

            }
        }

        return vi;
    }

    public int getCount() {
        if (App.getInstance().isToggled())
            return (App.getInstance().getItemList() != null) ? App
                    .getInstance().getItemList().size() + 1 : 1;
        else
            return 1;
    }

    public int getViewTypeCount() {
        return 2;
    }

    public int getItemViewType(int position) {
        if (position == 0)
            return 0;
        else
            return 1;
    }

    public void update() {
        this.notifyDataSetChanged();
    }

    public void setVersion_index(int version_index) {
        this.version_index = version_index;
    }

    public void setPath_index(int path_index) {
        this.path_index = path_index;
    }
}
