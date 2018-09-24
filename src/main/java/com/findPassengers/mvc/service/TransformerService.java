package com.findPassengers.mvc.service;

import com.findPassengers.mvc.entity.*;
import com.findPassengers.mvc.repository.*;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TransformerService {

    @Autowired
    private AenaflightSourceRepository aenaflightSourceRepository;
    @Autowired
    private AenaflightDestinationRepository aenaflightDestinationRepository;
    @Autowired
    private GroupsToTransformRepository groupsToTransformRepository;

    public void work(){
        List<GroupToTransform> groups;
        do {
            groups = groupsToTransformRepository.findTop1000By();
            processGroups(groups);
        } while ( !groups.isEmpty() );
    }

    private void processGroups(List<GroupToTransform> groups){
        for(GroupToTransform group: groups){
            List<Aenaflight2017> aenaflight2017List = aenaflightSourceRepository.findByFlightIcaoCodeAndFlightNumberAndSchdDepOnlyDateLtOrderByIdAsc(group.getFlight_icao_code(), group.getFlight_number(), group.getSchd_dep_only_date_lt());

            if(aenaflight2017List!=null && !aenaflight2017List.isEmpty()) {
                AenaflightDestination aenaflightDestination = new AenaflightDestination();


                Set<String> baggageInfoSet = new HashSet<>();
                Set<String> counterSet = new HashSet<>();
                Set<String> gateInfoSet = new HashSet<>();
                Set<String> loungeInfoSet = new HashSet<>();
                Set<String> terminalInfoSet = new HashSet<>();
                Set<String> arrTerminalInfoSet = new HashSet<>();
                for(Aenaflight2017 aenaflight2017: aenaflight2017List){
                    baggageInfoSet.add(aenaflight2017.getBaggage_info());
                    counterSet.add(aenaflight2017.getCounter());
                    gateInfoSet.add(aenaflight2017.getGate_info());
                    loungeInfoSet.add(aenaflight2017.getLounge_info());
                    terminalInfoSet.add(aenaflight2017.getTerminal_info());
                    arrTerminalInfoSet.add(aenaflight2017.getArr_terminal_info());
                }
                Aenaflight2017 aenaflight2017Last = aenaflight2017List.get(aenaflight2017List.size()-1);

                aenaflightDestination.setAdep(aenaflight2017Last.getDep_apt_code_iata());
                aenaflightDestination.setAdes(aenaflight2017Last.getArr_apt_code_iata());
                aenaflightDestination.setFlight_code(aenaflight2017Last.getFlightIcaoCode());
                aenaflightDestination.setFlight_number(aenaflight2017Last.getFlightNumber());
                aenaflightDestination.setCarrier_code(aenaflight2017Last.getCarrier_icao_code());
                aenaflightDestination.setCarrier_number(aenaflight2017Last.getCarrier_number());
                aenaflightDestination.setStatus_info(aenaflight2017Last.getStatus_info());
                aenaflightDestination.setSchd_dep_lt(toDate(aenaflight2017Last.getSchdDepOnlyDateLt(), aenaflight2017Last.getSchd_dep_only_time_lt()));
                aenaflightDestination.setSchd_arr_lt(toDate(aenaflight2017Last.getSchd_arr_only_date_lt(), aenaflight2017Last.getSchd_arr_only_time_lt()));
                aenaflightDestination.setEst_dep_lt(toDate(aenaflight2017Last.getEst_dep_date_time_lt()));
                aenaflightDestination.setEst_arr_lt(toDate(aenaflight2017Last.getEst_arr_date_time_lt()));
                aenaflightDestination.setAct_dep_lt(toDate(aenaflight2017Last.getAct_dep_date_time_lt()));
                aenaflightDestination.setAct_arr_lt(toDate(aenaflight2017Last.getAct_arr_date_time_lt()));
                aenaflightDestination.setFlt_leg_seq_no(Integer.valueOf(aenaflight2017Last.getFlt_leg_seq_no()));
                aenaflightDestination.setAircraft_name_scheduled(aenaflight2017Last.getAircraft_name_scheduled());
                aenaflightDestination.setBaggage_info(reverseJoin(baggageInfoSet));
                aenaflightDestination.setCounter(reverseJoin(counterSet));
                aenaflightDestination.setGate_info(reverseJoin(gateInfoSet));
                aenaflightDestination.setLounge_info(reverseJoin(loungeInfoSet));
                aenaflightDestination.setTerminal_info(reverseJoin(terminalInfoSet));
                aenaflightDestination.setArr_terminal_info(reverseJoin(arrTerminalInfoSet));
                aenaflightDestination.setSource_data(aenaflight2017Last.getSource_data());
                aenaflightDestination.setCreated_at(toDate(aenaflight2017Last.getCreated_at()));

                try {
                    aenaflightDestinationRepository.saveAndFlush(aenaflightDestination);
                    long id = aenaflightDestination.getId();
                } catch(Exception ex){
                    ex.printStackTrace();
                    break;
                }
            }
        }
    }

    private Date toDate(Long date){
        if(date !=null) {
            return new Date(date * 1000);
        } else {
            return null;
        }
    }

    private Date toDate(String date, String time){
        return toDate(date + " " + time);
    }

    private Date toDate(String date){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy hh:mm");
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    private String reverseJoin(Set<String> set){
        StringBuilder result = new StringBuilder();
        for(String baggage: set ) {
            if(result.length() >0 ){
                result.insert(0, ",");
            }
            result.insert(0, baggage);
        }
        return result.toString();
    }

}
