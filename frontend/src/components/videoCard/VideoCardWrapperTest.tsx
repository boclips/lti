import {User} from "boclips-api-client/dist/sub-clients/organisations/model/User";
import VideoCardWrapper from "./VideoCardWrapper";
import {VideoFactory} from "boclips-api-client/dist/test-support/VideosFactory";
import {SubjectFactory} from "boclips-api-client/dist/test-support/SubjectsFactory";
import {AttachmentFactory} from "boclips-api-client/dist/test-support/AttachmentsFactory";
import convertApiClientVideo from "../../service/video/convertVideoFromApi";
import React from "react";
import {UserFactory} from "boclips-api-client/dist/test-support/UserFactory";

describe('VideoCardWrapper', () => {
  const renderCard = (user: User) => {
    const video = VideoFactory.sample({
      id: '1',
      title: 'goats',
      subjects: [SubjectFactory.sample({ name: 'Design' })],
      attachments: [AttachmentFactory.sample({ id: 'i am attachment' })],
      ageRange: {
        min: 3,
        max: 5,
      },
      bestFor: [{
        label: 'Hook'
      }]

    })

    return <VideoCardWrapper video={convertApiClientVideo(video)} query='cats' actions={[]} user={user}/>
  }


  it('shows the new video card when user has correct flag', () => {
    const user = UserFactory.sample({features: })
  });
  it('shows the old video card when user does not have correct flag', () => {});
  it('shows the old video card when user flag is false', () => {});
  it('shows the old video card when no user exists', () => {});
})
