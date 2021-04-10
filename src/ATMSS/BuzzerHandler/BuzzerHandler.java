package ATMSS.BuzzerHandler;

import ATMSS.HWHandler.HWHandler;
import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.Msg;

public class BuzzerHandler extends HWHandler {
    private String id;
    private AppKickstarter appKickstarter;

    public BuzzerHandler(String id, AppKickstarter appKickstarter) {
        super(id, appKickstarter);
        this.id = id;
        this.appKickstarter = appKickstarter;
    }

    @Override
    protected void processMsg(Msg msg) {
        switch (msg.getType()){
            default:
                log.warning(id + ": unknown message type: [" + msg + "]");
        }
    }
}
