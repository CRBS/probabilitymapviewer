#!/usr/bin/env python

import sys
import os
import argparse
import boto3


def _parse_arguments(desc, theargs):
    """Parses command line arguments using argparse
    """
    help_formatter = argparse.RawDescriptionHelpFormatter
    parser = argparse.ArgumentParser(description=desc,
                                     formatter_class=help_formatter)
    parser.add_argument("--ownerid", default='099720109477',
                        help="Owner id to pass to search " +
                             "(default 099720109477)")
    parser.add_argument('--descriptionfilter',
                        default='Canonical, Ubuntu, 16.04 LTS, amd64 xenial image build on 2018-05-22',
                        help='Find only AMI image with this string in description' +
                             ' (default: Canonical, Ubuntu, 16.04 LTS, amd64 xenial image build on 2018-05-22')
    return parser.parse_args(theargs)

def _get_ami_mapping(theargs):
    """Returns a string containing ami mapping
    """
    mapstr = ''
    ec2 = boto3.client('ec2')
    response = ec2.describe_regions()
    for region in response['Regions']:
        rname = region['RegionName']
        sys.stdout.write('Running query in region: ' + rname + '\n')
        ec2 = boto3.client('ec2', region_name=rname)
        resp = ec2.describe_images(Owners=[theargs.ownerid],
                                   Filters=[{'Name': 'description',
                                             'Values': [theargs.descriptionfilter]},
                                             {'Name': 'virtualization-type',
                                             'Values': ['hvm']},
                                             {'Name': 'name',
                                              'Values': ['*ssd*']}])
        for image in resp['Images']:
           
            mapstr += ('           "' + rname + '"   : {"AMI" : "' +
                       image['ImageId'] + '"},\n')
    sys.stdout.write('\n\n Below is json fragment that can ' + 
                     'go in "RegionMap" of cloud formation template\n\n')
    return mapstr


def main(arglist):
    desc = """
              This script uses AWS boto library to query for AMI
              images that match the owner and name filter
              passed in via --ownerid and --namefilter flags for
              this tool.  The output is json fragment that can
              be put in the "Mappings" => "RegionMap" section

           """
    theargs = _parse_arguments(desc, sys.argv[1:])
    sys.stdout.write('Querying AWS: \n')
    sys.stdout.write(_get_ami_mapping(theargs))


if __name__ == '__main__': # pragma: no cover
    sys.exit(main(sys.argv))
